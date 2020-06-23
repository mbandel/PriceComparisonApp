package com.pc.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.pc.R;
import com.pc.adapter.LoginAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginRegisterActivity extends AppCompatActivity {

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        ButterKnife.bind(this);
        LoginAdapter loginAdapter = new LoginAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(loginAdapter);
        tabs.setupWithViewPager(viewPager);

    }



}