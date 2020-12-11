package com.pc.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import com.pc.PriceComparison;
import com.pc.R;
import com.pc.fragment.AddShoppingListFragment;
import com.pc.fragment.StoreFragment;
import com.pc.fragment.StoreMapFragment;
import com.pc.model.ShoppingList;
import com.pc.model.Store;
import com.pc.retrofit.Connector;
import com.pc.util.MenuNavigation;
import com.pc.util.NavigationAddShoppingList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddShoppingListActivity extends AppCompatActivity implements NavigationAddShoppingList {

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private Connector connector;
    private SharedPreferences sharedPreferences;
    private String token;
    private int userId;
    private AddShoppingListFragment addShoppingListFragment;
    private StoreMapFragment storeMapFragment;
    private List<Store> stores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_list);
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
        getStores();

    }

    @Override
    public void goToMap() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, storeMapFragment)
                .commit();
    }

    @Override
    public void goToEdition(Store store) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, addShoppingListFragment)
                .commit();
    }


    @Override
    public void addShoppingList(ShoppingList shoppingList) {
        Call<String> addShoppingListCall = connector.serverApi.addShoppingList(token, shoppingList);
        addShoppingListCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                PriceComparison.createSnackbar(drawer, getString(R.string.shopping_list_added)).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                PriceComparison.createSnackbar(drawer, getString(R.string.server_error)).show();
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
                    addShoppingListFragment = new AddShoppingListFragment(userId, stores);
                    storeMapFragment = new StoreMapFragment(stores);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, addShoppingListFragment)
                            .commit();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Store>> call, Throwable t) {
                PriceComparison.createSnackbar(drawer, getString(R.string.server_error)).show();
            }
        });
    }
}