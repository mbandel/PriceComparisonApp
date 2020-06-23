package com.pc.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pc.R;
import com.pc.fragment.LoginFragment;
import com.pc.fragment.RegisterFragment;

public class LoginAdapter extends FragmentPagerAdapter {
    private final Context context;

    public LoginAdapter(Context context, FragmentManager fm){
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
      if(position==0) {
          return  new LoginFragment();
      }else {
          return new RegisterFragment();
      }
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.login);
            case 1:
                return context.getResources().getString(R.string.register);
        }
        return null;
    }
}
