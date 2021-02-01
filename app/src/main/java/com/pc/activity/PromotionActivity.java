package com.pc.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.pc.PriceComparison;
import com.pc.R;
import com.pc.adapter.CategoryAdapter;
import com.pc.adapter.PromotionAdapter;
import com.pc.model.Category;
import com.pc.model.Poster;
import com.pc.model.Store;
import com.pc.retrofit.Connector;
import com.pc.util.MenuNavigation;
import com.pc.util.SortPoster;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromotionActivity extends AppCompatActivity {

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.list_of_promotions)
    RecyclerView recyclerView;
    @BindView(R.id.sort_btn)
    Button sortButton;
    @BindView(R.id.filter_stores_btn)
    Button filterStoreButton;

    private AppBarConfiguration mAppBarConfiguration;

    private Connector connector;
    private SharedPreferences sharedPreferences;
    private String token;
    private List<Poster> promotions = new ArrayList<>();
    private List<Store> stores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
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
        getStores();
        getPromotions();

    }


    public void getStores(){
        Call<List<Store>> storesCall = connector.serverApi.getStores(token);
        storesCall.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(@NotNull Call<List<Store>> call, @NotNull Response<List<Store>> response) {
                stores = response.body();
            }

            @Override
            public void onFailure(@NotNull Call<List<Store>> call, Throwable t) { }
        });
    }

    public void getPromotions(){
      Call<List<Poster>> posterCall = connector.serverApi.getAllPosters();
      posterCall.enqueue(new Callback<List<Poster>>() {
          @Override
          public void onResponse(Call<List<Poster>> call, Response<List<Poster>> response) {
              if (response.isSuccessful()) {
                  for (Poster poster : response.body()) {
                      if (poster.getPromotionDate() != null & poster.getPromotionPrice() != null) {
                          if (PriceComparison.isFutureDate(poster.getPromotionDate())){
                              promotions.add(poster);
                          }
                      }
                  }
                  showPromotions(promotions);
              }
          }

          @Override
          public void onFailure(Call<List<Poster>> call, Throwable t) {
              PriceComparison.createSnackbar(drawer, getString(R.string.server_error)).show();
          }
      });
    }


    public void showPromotions(List<Poster> list){
        PromotionAdapter promotionAdapter = new PromotionAdapter(this, list);
        recyclerView.setAdapter(promotionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        RadioButton priceDesc = dialog.findViewById(R.id.price_desc);
        RadioButton ratingDesc = dialog.findViewById(R.id.rating_desc);

        dialog.show();
        sortButton.setOnClickListener(view -> {
            if(sortOption.getCheckedRadioButtonId() == priceDesc.getId())
                showPromotions(SortPoster.sortByPrice(promotions, true));
            else if(sortOption.getCheckedRadioButtonId() == ratingDesc.getId())
                showPromotions(SortPoster.sortByRating(promotions, false));
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
                showPromotions(SortPoster.filterByStores(promotions, checkedStores));
            }
            dialog.dismiss();
        });
    }
}



