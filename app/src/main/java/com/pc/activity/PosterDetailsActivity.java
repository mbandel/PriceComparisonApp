package com.pc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.Share;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.pc.R;
import com.pc.adapter.CommentAdapter;
import com.pc.model.Comment;
import com.pc.model.Poster;
import com.pc.model.Rating;
import com.pc.model.User;
import com.pc.retrofit.Connector;
import com.pc.util.MenuNavigation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosterDetailsActivity extends AppCompatActivity {

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.product_title)
    TextView title;
    @BindView(R.id.price_value)
    TextView price;
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

    private int posterId;
    private int userId;
    private Connector connector;
    private SharedPreferences sharedPreferences;
    private String token;
    private boolean isRated;
    private int currentRatingValue;
    private int ratingId;


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

    public void fillData(Poster poster){
        title.setText(poster.getProduct().getName());
        price.setText(poster.getPrice().toString());
        storeName.setText(poster.getStore().getName());
        storeAddress.setText(poster.getStore().getAddress());
        price.setText(String.format("%.2f", poster.getPrice()) + " zł");
        rating.setText(String.valueOf(poster.getRatingValue()));
        if (poster.getRatingValue() > 0){
            rating.setTextColor(getColor(R.color.colorGreen));
        }else if (poster.getRatingValue() < 0) {
            rating.setTextColor(getColor(R.color.colorRed));
        }
        like.setOnClickListener(view -> {
            if (!isRated) {
                like.setEnabled(false);
                addRating(1);
                getCommentsByPosterId(posterId);
                System.out.println("add rating");
            }
            else if (isRated && currentRatingValue == -1) {
                dislike.setEnabled(false);
                editRating(1);
                System.out.println("edit rating");
            }
        });
        dislike.setOnClickListener(view -> {
            if (!isRated) {
                dislike.setBackground(getDrawable(R.drawable.orange_border));
                addRating(-1);
                System.out.println("add rating");
            }
            else if (isRated && currentRatingValue == 1) {
                System.out.println("edit rating");
                editRating(-1);
            }
        });
    }

    public void getPosterById(int id){
        Call<Poster> posterCall = connector.serverApi.getPosterById(token, id);
        posterCall.enqueue(new Callback<Poster>() {
            @Override
            public void onResponse(Call<Poster> call, Response<Poster> response) {
                if (response.isSuccessful()){
                    fillData(response.body());
                } else {
                    Toast.makeText(getApplicationContext(), "Nie ma takiego ogłoszenia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Poster> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editRating(int value){
        Rating rating = new Rating(value);
        Call<String> editRatingCall = connector.serverApi.editRating(token, ratingId, rating);
        editRatingCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("PUT: "+ response.body());
                if(response.isSuccessful()) {
                   setButtonBackground(value);
                   getPosterById(posterId);
               }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Dodano komentarz", Toast.LENGTH_SHORT).show();
                    sendButton.setEnabled(true);
                    runOnUiThread(() -> {
                        getCommentsByPosterId(posterId);
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(), "Już dodałeś 1 komentarz", Toast.LENGTH_SHORT).show();
                    sendButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                sendButton.setEnabled(true);
            }
        });
    }

    @OnClick(R.id.send_btn)
    public void onSendButtonClick(){
        sendButton.setEnabled(false);
        addComment(posterId, sharedPreferences.getInt("id", 0));
    }

}