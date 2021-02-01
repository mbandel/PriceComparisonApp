package com.pc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.pc.PriceComparison;
import com.pc.R;
import com.pc.adapter.CommentAdapter;
import com.pc.fragment.AddPromotionFragment;
import com.pc.fragment.AddToListFragment;
import com.pc.model.Comment;
import com.pc.model.Poster;
import com.pc.model.Rating;
import com.pc.model.ShoppingList;
import com.pc.model.User;
import com.pc.retrofit.Connector;
import com.pc.util.AddToListDialog;
import com.pc.util.MenuNavigation;
import com.pc.util.NavigationAddPromotion;
import com.pc.util.NavigationAddToList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosterDetailsActivity extends AppCompatActivity implements NavigationAddToList, NavigationAddPromotion {

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.product_title)
    TextView title;
    @BindView(R.id.price_value)
    TextView price;
    @BindView(R.id.discount_value)
    TextView discountValue;
    @BindView(R.id.discount)
    ImageView discount;
    @BindView(R.id.store_name_value)
    TextView storeName;
    @BindView(R.id.store_address_value)
    TextView storeAddress;
    @BindView(R.id.rating_value)
    TextView rating;
    @BindView(R.id.like)
    ImageView like;
    @BindView(R.id.dislike)
    ImageView dislike;
    @BindView(R.id.comments_list)
    RecyclerView commentsRecyclerView;
    @BindView(R.id.add_comment_input)
    TextView content;
    @BindView(R.id.send_btn)
    MaterialButton sendButton;
    @BindView(R.id.add_to_list)
    FloatingActionButton addToListButton;

    private int posterId;
    private int userId;
    private Connector connector;
    private SharedPreferences sharedPreferences;
    private String token;
    private boolean isRated;
    private int currentRatingValue;
    private int ratingId;
    private List<ShoppingList> shoppingLists;
    private ShoppingList selectedShoppingList;
    private Poster poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        MenuNavigation menuNavigation = new MenuNavigation(this);
        navigationView.setNavigationItemSelectedListener(menuNavigation);

        sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        connector = Connector.getInstance();
        token = sharedPreferences.getString("token", null);
        posterId = getIntent().getExtras().getInt("id");
        userId = sharedPreferences.getInt("id", 0);

        getPosterById(posterId);
        getCommentsByPosterId(posterId);
        getRating();
    }

    @Override
    public void addToList() {
        if (selectedShoppingList == null){
            Toast.makeText(getApplicationContext(), "Nie wybrałeś listy zakupów", Toast.LENGTH_SHORT).show();
        }
        else {
            Call<String> addPosterCall = connector.serverApi.addPosterToShoppingList(token, selectedShoppingList.getId(), poster);
            addPosterCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        PriceComparison.createSnackbar(drawer, getString(R.string.added_to_shopping_list)).show();
                        selectedShoppingList = null;
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    PriceComparison.createSnackbar(drawer, getString(R.string.server_error)).show();
                }
            });
        }
    }

    @Override
    public void selectList(ShoppingList shoppingList) {
        selectedShoppingList = shoppingList;
    }

    @Override
    public void addPromotion(Poster poster) {
        Call<String> addPromotionCall = connector.serverApi.addPromotion(token, posterId, poster);
        addPromotionCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                PriceComparison.createSnackbar(drawer, "Dodano promocję").show();
                fragmentContainer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                PriceComparison.createSnackbar(drawer, getString(R.string.server_error)).show();
            }
        });
    }

    @Override
    public void exit() {
        fragmentContainer.setVisibility(View.GONE);
        selectedShoppingList = null;
    }

    public void fillData(Poster poster) {
        title.setText(poster.getProduct().getName());
        price.setText(poster.getPrice().toString());
        storeName.setText(poster.getStore().getName());
        storeAddress.setText(poster.getStore().getAddress());
        price.setText(String.format(Locale.ENGLISH, "%.2f zł", poster.getPrice()));
        rating.setText(String.valueOf(poster.getRatingValue()));
        if (poster.getRatingValue() > 0) {
            rating.setTextColor(getColor(R.color.colorGreen));
        } else if (poster.getRatingValue() < 0) {
            rating.setTextColor(getColor(R.color.colorRed));
        }
        like.setOnClickListener(view -> {
            if (!isRated) {
                like.setEnabled(false);
                addRating(1);
                getCommentsByPosterId(posterId);
            } else if (isRated && currentRatingValue == -1) {
                dislike.setEnabled(false);
                editRating(1);
            }
        });
        dislike.setOnClickListener(view -> {
            if (!isRated) {
                dislike.setBackground(getDrawable(R.drawable.orange_border));
                addRating(-1);
            } else if (isRated && currentRatingValue == 1) {
                editRating(-1);
            }
        });


        if (poster.getPromotionDate() == null && poster.getPromotionPrice() == null) {
            discount.setImageResource(R.drawable.ic_discount);
            discount.setOnClickListener(view -> {
                AddPromotionFragment fragment = new AddPromotionFragment(posterId, poster.getPrice());
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                fragmentContainer.setVisibility(View.VISIBLE);
            });
        } else {
            if (PriceComparison.isFutureDate(poster.getPromotionDate())) {
                price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                discountValue.setText(String.format(Locale.ENGLISH, "%.2f zł ważne do:\n%s", poster.getPromotionPrice(), poster.getPromotionDate()));
            } else {
                discount.setImageResource(R.drawable.ic_discount);
                discount.setOnClickListener(view -> {
                    AddPromotionFragment fragment = new AddPromotionFragment(posterId, poster.getPrice());
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                    fragmentContainer.setVisibility(View.VISIBLE);
                });
            }
        }

    }


    public void getPosterById(int id){
        Call<Poster> posterCall = connector.serverApi.getPosterById(token, id);
        posterCall.enqueue(new Callback<Poster>() {
            @Override
            public void onResponse(Call<Poster> call, Response<Poster> response) {
                if (response.isSuccessful()){
                    fillData(response.body());
                    poster = response.body();
                    getShoppingLists();
                } else {
                    Toast.makeText(getApplicationContext(), "Nie ma takiego ogłoszenia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Poster> call, Throwable t) {
                PriceComparison.createSnackbar(drawer, getString(R.string.server_error)).show();
                sendButton.setEnabled(false);
            }
        });
    }

    public void getCommentsByPosterId(int id){
        Call<List<Comment>> commentCall = connector.serverApi.getCommentsByPosterId(token, id);
        commentCall.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful()){
                    if(!response.body().isEmpty())
                        showComments(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

            }
        });
    }

    public void getRating(){
        Call<Rating> ratingCall = connector.serverApi.getRatingByUserAndPoster(token, posterId, userId);
        ratingCall.enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {
                if (response.isSuccessful() && response.body() != null){
                    isRated = true;
                    Rating rating = response.body();
                    ratingId = rating.getId();
                    System.out.println("RatingID: " + ratingId);
                    if(rating.getValue() == 1){
                        like.setBackground(getDrawable(R.drawable.orange_border));
                        like.setEnabled(false);
                        dislike.setEnabled(true);
                        currentRatingValue = 1;
                        System.out.println("RatingValue: " + rating.getValue());
                    } else if (rating.getValue() == -1) {
                        dislike.setBackground(getDrawable(R.drawable.orange_border));
                        dislike.setEnabled(false);
                        like.setEnabled(true);
                        currentRatingValue = -1;
                        System.out.println("RatingValue: " + rating.getValue());
                    }
                }else{
                    isRated = false;
                    like.setEnabled(true);
                    dislike.setEnabled(true);
                }

            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) { }
        });
    }

    public void addRating(int value){
        User user = new User(userId);
        Poster poster = new Poster(posterId);
        Rating rating = new Rating(value, user, poster);
        Call<String> addRatingCall = connector.serverApi.addRating(token,  rating);
        addRatingCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                setButtonBackground(value);
                getPosterById(posterId);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                PriceComparison.createSnackbar(drawer, getString(R.string.server_error)).show();
            }
        });
    }

    public void editRating(int value){
        Rating rating = new Rating(value);
        Call<String> editRatingCall = connector.serverApi.editRating(token, ratingId, rating);
        editRatingCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.isSuccessful()) {
                   setButtonBackground(value);
                   getPosterById(posterId);
               }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                PriceComparison.createSnackbar(drawer, getString(R.string.server_error)).show();
            }
        });
    }

    public void setButtonBackground(int value){
        if (value == 1){
            like.setEnabled(false);
            like.setBackground(getDrawable(R.drawable.orange_border));
            dislike.setBackground(getDrawable(R.drawable.white_border));
            dislike.setEnabled(true);
        }
        else if (value == -1) {
            dislike.setBackground(getDrawable(R.drawable.orange_border));
            like.setBackground(getDrawable(R.drawable.white_border));
            like.setEnabled(true);
            dislike.setEnabled(false);
        }
    }

    public void showComments(List<Comment> comments){
        CommentAdapter commentAdapter = new CommentAdapter(this, comments);
        commentsRecyclerView.setAdapter(commentAdapter);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    public void addComment(int posterId, int userId){
        User user = new User(userId);
        Poster poster = new Poster(posterId);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormat.format(date);
        Comment comment = new Comment(content.getText().toString(), user, dateString, poster);
        Call<String> commentCall = connector.serverApi.addComment(token, comment);
        commentCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    PriceComparison.createSnackbar(drawer, "Dodano komentarz").show();
                    sendButton.setEnabled(true);
                    runOnUiThread(() -> {
                        getCommentsByPosterId(posterId);
                    });
                }
                else {
                    PriceComparison.createSnackbar(drawer, "Już dodałeś 1 komentarz").show();
                    sendButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                PriceComparison.createSnackbar(drawer, getString(R.string.server_error)).show();
                sendButton.setEnabled(true);
            }
        });
    }

    public void getShoppingLists() {
        Call<List<ShoppingList>> shoppingListsCall = connector.serverApi.getShoppingListsByUserId(token, userId);
        shoppingLists = new ArrayList<>();
        shoppingListsCall.enqueue(new Callback<List<ShoppingList>>() {
            @Override
            public void onResponse(Call<List<ShoppingList>> call, Response<List<ShoppingList>> response) {
                if (response.isSuccessful()){
                    if (!response.body().isEmpty()) {
                        for (ShoppingList shoppingList : response.body()) {
                            if (shoppingList.getStore().getId() == poster.getStore().getId()) {
                                shoppingLists.add(shoppingList);
                            }
                        }
                        if (shoppingLists.isEmpty()) {
                            addToListButton.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ShoppingList>> call, Throwable t) {

            }
        });

    }

    @OnClick(R.id.send_btn)
    public void onSendButtonClick(){
        sendButton.setEnabled(false);
        addComment(posterId, sharedPreferences.getInt("id", 0));
    }

    @OnClick(R.id.add_to_list)
    public void onAddToListClick() {
        if (!shoppingLists.isEmpty()) {
            AddToListFragment fragment = new AddToListFragment(shoppingLists);
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
            fragmentContainer.setVisibility(View.VISIBLE);
        } else {
            PriceComparison.createSnackbar(drawer, getString(R.string.no_shopping_list)).show();
        }
    }

}