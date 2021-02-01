package com.pc.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.pc.PriceComparison;
import com.pc.R;
import com.pc.adapter.ShoppingListAdapter;
import com.pc.model.Poster;
import com.pc.model.ShoppingList;
import com.pc.retrofit.Connector;
import com.pc.util.MenuNavigation;
import com.pc.util.NavigationShoppingList;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class ShoppingListActivity extends AppCompatActivity implements NavigationShoppingList {

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.shopping_list_spinner)
    AutoCompleteTextView shoppingListSpinner;
    @BindView(R.id.store_name_value)
    TextView storeName;
    @BindView(R.id.price_value)
    TextView priceValue;
    @BindView(R.id.shopping_list_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.expand_text)
    TextView expandText;
    @BindView(R.id.expand_btn)
    ImageView expandButton;
    @BindView(R.id.bottom_layout)
    View bottomLayout;
    @BindView(R.id.bottom_sheet_layout)
    View bottomConstraintLayout;

    private List<ShoppingList> shoppingLists;
    private SharedPreferences sharedPreferences;
    private Connector connector;
    private String token;
    private String[] shoppingListNames;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
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

        getShoppingLists();
        bottomSheetBehavior = BottomSheetBehavior.from(bottomConstraintLayout);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick({R.id.expand_text, R.id.expand_btn})
    public void onExpandTextClick() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            expandButton.setImageResource(R.drawable.ic_collapse);
            expandText.setText(R.string.collapse);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            expandButton.setImageResource(R.drawable.ic_expand);
            expandText.setText(R.string.expand);
        }
    }

    @OnClick(R.id.add_btn)
    public void onAddButtonClick() {
        startActivity(new Intent(getApplicationContext(), AddShoppingListActivity.class));
    }

    private void getShoppingLists() {
        Call<List<ShoppingList>> shoppingListCall = connector.serverApi.getShoppingListsByUserId(token, sharedPreferences.getInt("id" ,-1));
        shoppingListCall.enqueue(new Callback<List<ShoppingList>>() {
            @Override
            public void onResponse(Call<List<ShoppingList>> call, Response<List<ShoppingList>> response) {
                if (response.isSuccessful()) {
                    shoppingLists = response.body();
                    if (!shoppingLists.isEmpty()) {
                        fillData(response.body().get(0));
                        setShoppingListAdapter(shoppingLists);
                    }

                }
            }

            @Override
            public void onFailure(Call<List<ShoppingList>> call, Throwable t) {
                PriceComparison.createSnackbar(findViewById(R.id.constraint_layout), getString(R.string.server_error) ).show();
            }
        });
    }


    private void fillData(ShoppingList shoppingList) {
        ShoppingListAdapter shoppingListAdapter = new ShoppingListAdapter(this, shoppingList.getPosters(), shoppingList.getId());
        recyclerView.setAdapter(shoppingListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        storeName.setText(String.format("%s\n%s", shoppingList.getStore().getName(), shoppingList.getStore().getAddress()));

        priceValue.setText(String.format("%s zł", priceSum(shoppingList.getPosters())));
    }

    private String priceSum(List<Poster> posters) {
        double sum = 0.0;
        for (Poster poster: posters) {
            sum += poster.getPrice();
        }
        return String.format(Locale.ENGLISH, "%.2f", sum);
    }

    private void setShoppingListAdapter(List<ShoppingList> shoppingLists) {
        shoppingListNames = new String[shoppingLists.size()];
        for (int i = 0; i < shoppingLists.size(); i++) {
            shoppingListNames[i] = shoppingLists.get(i).getName();
        }
        shoppingListSpinner.setText(shoppingLists.get(0).getName());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_spinner, shoppingListNames);
        shoppingListSpinner.setAdapter(adapter);
        shoppingListSpinner.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> fillData(shoppingLists.get(i)));
    }

    @Override
    public void refresh() {
        getShoppingLists();
    }
}