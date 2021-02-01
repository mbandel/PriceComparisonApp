package com.pc.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.pc.PriceComparison;
import com.pc.R;
import com.pc.adapter.CategoryAdapter;
import com.pc.fragment.EditEmailFragment;
import com.pc.fragment.EditNameFragment;
import com.pc.fragment.EditPasswordFragment;
import com.pc.fragment.ProfileNavigationFragment;
import com.pc.model.Category;
import com.pc.model.User;
import com.pc.retrofit.Connector;
import com.pc.util.MenuNavigation;
import com.pc.util.NavigationEditProfile;
import com.pc.util.NavigationFragment;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements NavigationEditProfile, NavigationFragment {

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
    private EditEmailFragment editEmailFragment = new EditEmailFragment();
    private EditPasswordFragment editPasswordFragment = new EditPasswordFragment();
    private EditNameFragment editNameFragment = new EditNameFragment();
    private ProfileNavigationFragment profileNavigationFragment;
    private boolean backToFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        getUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUser();
    }

    @Override
    public void onBackPressed() {
        if (backToFragment) {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container, profileNavigationFragment)
                    .commit();
        }
        backToFragment = false;
    }

    @Override
    public void editName(String firstName, String lastName) {
        User user = new User(firstName, lastName);
        Call<String> editUserCall = connector.serverApi.editUser(token, userId, user);
        editUserCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    showDialog("Pomyślnie zmieniono dane");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                PriceComparison.createSnackbar(drawer, getString(R.string.server_error)).show();
            }
        });
    }

    @Override
    public void editEmail(String username) {
        editUserData(username, 1);
    }

    @Override
    public void editPassWord(String password) {
        editUserData(password, 2);
    }



    @Override
    public void fragmentEditEmail() {
        backToFragment = true;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editEmailFragment)
                .commit();
    }

    @Override
    public void fragmentEditName() {
        backToFragment = true;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editNameFragment)
                .commit();
    }

    @Override
    public void fragmentEditPassword() {
        backToFragment = true;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editPasswordFragment)
                .commit();
    }


    public void getUser() {
        Call<User> userCall = connector.serverApi.findUserById(token, userId);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    profileNavigationFragment = new ProfileNavigationFragment(user);
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragment_container, profileNavigationFragment)
                            .commit();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                PriceComparison.createSnackbar(drawer, getString(R.string.server_error)).show();
            }
        });
    }

    public void editUserData(String userData, int option) {
        User user = new User(userData, option);
        Call<String> editUserCall = connector.serverApi.editUser(token, userId, user);
        editUserCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    switch (option) {
                        case 1:
                            showDialog("Pomyślnie zmieniono adres e-mail");
                            break;
                        case 2:
                            showDialog("Pomyślnie zmieniono hasło");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                PriceComparison.createSnackbar(drawer, getString(R.string.server_error)).show();
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