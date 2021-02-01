package com.pc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.pc.R;
import com.pc.fragment.ProductPriceFragment;
import com.pc.fragment.StoreFragment;
import com.pc.fragment.StoreMapFragment;
import com.pc.fragment.SummaryFragment;
import com.pc.model.Poster;
import com.pc.model.Product;
import com.pc.model.Store;
import com.pc.model.User;
import com.pc.retrofit.Connector;
import com.pc.util.MenuNavigation;
import com.pc.util.NavigationAddPoster;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPosterActivity extends AppCompatActivity implements NavigationAddPoster {

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private Connector connector;
    private SharedPreferences sharedPreferences;
    private String token;
    private List<Product> products;
    private List<Store> stores;
    private ProductPriceFragment productPriceFragment;
    private StoreFragment storeFragment;
    private StoreMapFragment storeMapFragment;
    private Poster poster;
    private SummaryFragment summaryFragment;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poster);
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
        userId = sharedPreferences.getInt("id", 0);
        poster = new Poster();
        summaryFragment = new SummaryFragment(poster);
        getProducts();
        getStores();
    }

    @Override
    public void goToProduct() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, productPriceFragment).commit();
    }

    @Override
    public void goToStore() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, storeFragment).commit();
    }

    @Override
    public void goToMap() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, storeMapFragment).commit();
    }

    @Override
    public void goToSummary() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, summaryFragment).commit();
    }


    @Override
    public void addPoster(Poster posterData) {
        posterData.setUser(new User(userId));
        Call addPosterCall = connector.serverApi.addPoster(token, posterData);
        addPosterCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    showDialog(getString(R.string.poster_added));
                } else {
                    showDialog(getString(R.string.poster_exist));
                }
            }

            @Override
            public void onFailure(@NotNull Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getProducts(){
        Call<List<Product>> productsCall = connector.serverApi.getProducts(token);
        productsCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    products = response.body();
                    productPriceFragment = new ProductPriceFragment(products, poster);
                    goToProduct();
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }

    public void getStores(){
        Call<List<Store>> storeCall = connector.serverApi.getStores(token);
        storeCall.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(@NotNull Call<List<Store>> call, @NotNull Response<List<Store>> response) {
                if(response.isSuccessful()) {
                    stores = response.body();
                    storeFragment = new StoreFragment(stores, poster);
                    storeMapFragment = new StoreMapFragment(stores, poster);
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Store>> call, Throwable t) {
            }
        });
    }

    public void showDialog(String title) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_dialog);
        TextView titleTV = dialog.findViewById(R.id.title);
        titleTV.setText(title);
        MaterialButton back_button = dialog.findViewById(R.id.back_btn);
        back_button.setOnClickListener(view -> {
            dialog.dismiss();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        });
        dialog.show();
    }


}