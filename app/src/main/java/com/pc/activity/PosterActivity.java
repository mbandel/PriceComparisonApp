package com.pc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.pc.R;
import com.pc.adapter.ProductAdapter;
import com.pc.fragment.MapsFragment;
import com.pc.fragment.PosterFragment;
import com.pc.model.Poster;
import com.pc.model.Product;
import com.pc.model.Store;
import com.pc.model.Token;
import com.pc.retrofit.Connector;
import com.pc.util.MenuNavigation;
import com.pc.util.SortPoster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.bottom_nav_view)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.title)
    TextView productTitle;
    @BindView(R.id.sort_btn)
    Button sortButton;
    @BindView(R.id.filter_stores_btn)
    Button filterStoreButton;

    private Connector connector;
    private SharedPreferences sharedPreferences;
    private String token;
    private int productId;
    private PosterFragment posterFragment;
    private MapsFragment mapsFragment;
    private boolean fragmentCreated;
    private List<Poster> posters;
    private List<Store> stores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        MenuNavigation menuNavigation = new MenuNavigation(this);
        navigationView.setNavigationItemSelectedListener(menuNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        connector = Connector.getInstance();
        token = sharedPreferences.getString("token", null);
        productId = getIntent().getExtras().getInt("id");
        productTitle.setText(getIntent().getExtras().getString("name"));
        getPostersByProductId(productId);
        getStores();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPostersByProductId(productId);
    }

    public void getPostersByProductId(int id){
        Call<List<Poster>> postersCall = connector.serverApi.getPostersByProductId(token, id);
        postersCall.enqueue(new Callback<List<Poster>>() {
            @Override
            public void onResponse(Call<List<Poster>> call, Response<List<Poster>> response) {
                if(response.isSuccessful()){
                    posters = response.body();
                    showPosters(posters);
                    mapsFragment = MapsFragment.newInstance(posters);
                }
            }

            @Override
            public void onFailure(Call<List<Poster>> call, Throwable t) { }
        });
    }

    public void showPosters(List<Poster> posterList){
        posterFragment = PosterFragment.newInstance(posterList);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, posterFragment).commit();
    }

    public void getStores(){
        Call<List<Store>> storesCall = connector.serverApi.getStores(token);
        storesCall.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                stores = response.body();
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @OnClick(R.id.sort_btn)
    public void onSortButtonClick(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.sort_dialog);
        RadioGroup sortOption = dialog.findViewById(R.id.option_sort);
        Button sortButton = dialog.findViewById(R.id.sort_btn);
        Button exitButton = dialog.findViewById(R.id.exit_btn);
        //RadioButton priceAsc = dialog.findViewById(R.id.price_asc);
        RadioButton priceDesc = dialog.findViewById(R.id.price_desc);
        //RadioButton ratingAsc = dialog.findViewById(R.id.rating_asc);
        RadioButton ratingDesc = dialog.findViewById(R.id.rating_desc);

        dialog.show();
        sortButton.setOnClickListener(view -> {
            if(sortOption.getCheckedRadioButtonId() == priceDesc.getId())
                showPosters(SortPoster.sortByPrice(posters, true));
            else if(sortOption.getCheckedRadioButtonId() == ratingDesc.getId())
                showPosters(SortPoster.sortByRating(posters, false));
           else if(sortOption.getCheckedRadioButtonId() == -1) {}

           dialog.dismiss();
        });

        exitButton.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }

    @OnClick(R.id.filter_stores_btn)
    public void onFilterButtonClick(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.store_filter_dialog);
        RadioGroup filterOption = dialog.findViewById(R.id.option_filter);
        Button exitButton = dialog.findViewById(R.id.exit_btn);
        Button chooseButton = dialog.findViewById(R.id.choose_btn);

        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        ArrayList<String> storeNames = new ArrayList<>();
        if(stores != null) {
            for(Store store : stores){
                if (!storeNames.contains(store.getName())) {
                    storeNames.add(store.getName());
                }
            }

            if(storeNames.size() > 0)
            for(int i=0; i<storeNames.size(); i++){
                CheckBox checkbox = new CheckBox(this);
                checkbox.setText(storeNames.get(i));
                filterOption.addView(checkbox);
                checkBoxes.add(checkbox);
            }
        }
        dialog.show();

        exitButton.setOnClickListener(view -> {
            dialog.dismiss();
        });

        ArrayList<String> checkedStores = new ArrayList<>();
        chooseButton.setOnClickListener(view -> {
            for(CheckBox checkBox : checkBoxes){
                if (checkBox.isChecked()){
                    checkedStores.add(checkBox.getText().toString());
                }
            }
            if (checkedStores.isEmpty()){
                Toast.makeText(this, "Nie wybrano żadnego sklepu", Toast.LENGTH_SHORT).show();
            }else {
                showPosters(SortPoster.filterByStores(posters, checkedStores));
            }
            dialog.dismiss();
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case(R.id.bottom_nav_list):
                if(posterFragment != null) {
                    productTitle.setVisibility(View.VISIBLE);
                    sortButton.setVisibility(View.VISIBLE);
                    filterStoreButton.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, posterFragment).commit();
                    break;
                }
            case (R.id.bottom_nav_map):
                if(mapsFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapsFragment).commit();
                    filterStoreButton.setVisibility(View.GONE);
                    productTitle.setVisibility(View.GONE);
                    sortButton.setVisibility(View.GONE);
                    break;
                }
        }
        return true;
    }

}