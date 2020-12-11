package com.pc.util;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
import com.pc.R;
import com.pc.activity.AddPosterActivity;
import com.pc.activity.MainActivity;
import com.pc.activity.ShoppingListActivity;

public class MenuNavigation implements NavigationView.OnNavigationItemSelectedListener {

    private Activity activity;

    public MenuNavigation(Activity activity){
        this.activity = activity;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case (R.id.nav_list):
                activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
                break;
            case(R.id.nav_add):
                activity.startActivity(new Intent(activity.getApplicationContext(), AddPosterActivity.class));
                break;
            case (R.id.nav_promotion):
                break;
            case (R.id.nav_shopping):
                activity.startActivity(new Intent(activity.getApplicationContext(), ShoppingListActivity.class));
                break;
        }
        return true;
    }
}
