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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.pc.retrofit.Connector;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    private Connector connector;
    private SharedPreferences sharedPreferences;
    private String token;
    private int productId;
    private PosterFragment posterFragment;
    private MapsFragment mapsFragment;
    private boolean fragmentCreated;


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
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        connector = Connector.getInstance();
        token = sharedPreferences.getString("token", null);
        productId = getIntent().getExtras().getInt("id");


        getPostersByProductId(productId);

    }


    public void getPostersByProductId(int id){
        Call<List<Poster>> postersCall = connector.serverApi.getPostersByProductId(token, id);
        postersCall.enqueue(new Callback<List<Poster>>() {
            @Override
            public void onResponse(Call<List<Poster>> call, Response<List<Poster>> response) {
                if(response.isSuccessful()){
                    List<Poster> posters = response.body();
                    posterFragment = PosterFragment.newInstance(posters);
                    fragmentCreated=true;
                    mapsFragment = MapsFragment.newInstance(posters);
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, posterFragment).commit();

                    //Toast.makeText(getApplicationContext(), "poster response", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Poster>> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case (R.id.nav_gallery):
                startActivity(new Intent(getApplicationContext(), GalleryActivity.class));
                break;
            case(R.id.bottom_nav_list):
                if(fragmentCreated)
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, posterFragment).commit();
                break;
            case (R.id.bottom_nav_map):
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapsFragment).commit();
                break;
        }
        return true;
    }

}