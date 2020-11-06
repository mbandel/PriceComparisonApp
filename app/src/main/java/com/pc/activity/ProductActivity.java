package com.pc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.pc.R;
import com.pc.adapter.ProductAdapter;
import com.pc.model.Product;
import com.pc.retrofit.Connector;
import com.pc.util.MenuNavigation;
import com.pc.util.SortProduct;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity {


    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.list_of_products)
    RecyclerView recyclerView;

    private Connector connector;
    private SharedPreferences sharedPreferences;
    private String token;
    private int id;
    private List<Product> products = new ArrayList<>();
    private boolean showLinear, showGrid, sortAsc, listCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
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

        id = getIntent().getExtras().getInt("id");
        getProductsByCategoryId(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProductsByCategoryId(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @OnTextChanged(R.id.search_edit_text)
    public void onSearchInputChange(CharSequence charSequence) {
        if (!charSequence.toString().trim().equals("")){
            showProductList(SortProduct.filterByProductName(products, charSequence.toString()));
        } else {
            showProductList(products);
        }
    }

    @OnClick(R.id.sort_az)
    void onSortClick() {
        if (sortAsc == true){
            sortAsc = false;
        }else{
            sortAsc = true;
        }
        if(listCreated)
            showProductList(SortProduct.sortAlphabetically(products, sortAsc));
    }


    @OnClick(R.id.grid)
    void setGridLayout(){
        showGrid = true;
        showLinear = false;
        if (listCreated)
            showProductGridList(products);
    }

    @OnClick(R.id.linear)
    void setLinearLayout(){
        showGrid = false;
        showLinear = true;
        if (listCreated)
            showProductLinearList(products);
    }

    public void getProductsByCategoryId(int id) {
        Call<List<Product>> productsCall = connector.serverApi.getProductsByCategoryId(token, id);
        productsCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    products = response.body();
                    listCreated = true;
                    showLinear = true;
                    showProductList(products);
                }else {
                    Toast.makeText(getApplicationContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showProductList(List<Product> products) {
        if (showLinear) {
            showProductLinearList(products);
        } else {
            showProductGridList(products);
        }
    }

    public void showProductLinearList(List<Product> products) {
        ProductAdapter productAdapter = new ProductAdapter(this, products);
        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void showProductGridList(List<Product> products) {
        ProductAdapter productAdapter = new ProductAdapter(this, products);
        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }
}