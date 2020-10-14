package com.pc.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.pc.R;
import com.pc.adapter.CategoryAdapter;
import com.pc.model.Category;
import com.pc.model.Product;
import com.pc.retrofit.Connector;
import com.pc.util.MenuNavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.list_of_categories)
    RecyclerView recyclerView;

    private AppBarConfiguration mAppBarConfiguration;

    private Connector connector;
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        getCategories();

    }


    public void getCategories(){
        Call<List<Category>> categoriesCall = connector.serverApi.getCategories(token);
        categoriesCall.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if(response.isSuccessful()) {
                    List<Category> categories = response.body();
                    showCategoriesList(categories);
                }
                else
                    Toast.makeText(getApplicationContext(), "code "+response.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
            }
        });
    }


    public void showCategoriesList(List<Category> list){
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, list);
        recyclerView.setAdapter(categoryAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    public int setIcon(String iconType){
        if (iconType.equals("słodycze"))
            return R.drawable.ic_candy;
        else if (iconType.equals("pieczywo"))
            return R.drawable.ic_bread;
        else if (iconType.equals("napoje"))
            return R.drawable.ic_drinks;
        else if (iconType.equals("warzywa"))
            return R.drawable.ic_vegetable;
        else if (iconType.equals("owoce"))
            return R.drawable.ic_fruit;
        else if (iconType.equals("przyprawy"))
            return R.drawable.spices;
        else if (iconType.equals("alkohole"))
            return R.drawable.ic_alcohol;
        else if (iconType.equals("nabiał"))
            return R.drawable.ic_milk;
        else if (iconType.equals("meat"))
            return R.drawable.ic_meat;
        else if (iconType.equals("ryby"))
            return R.drawable.ic_fish;
        else return R.drawable.border_bg;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
