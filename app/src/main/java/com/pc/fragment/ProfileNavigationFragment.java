package com.pc.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pc.R;
import com.pc.model.User;
import com.pc.util.NavigationFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProfileNavigationFragment extends Fragment {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.edit_data)
    TextView editData;
    @BindView(R.id.edit_email)
    TextView editEmail;

   private User user;
   private NavigationFragment navigation;

    public ProfileNavigationFragment(User user) {
        this.user = user;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NavigationFragment) {
            navigation = (NavigationFragment) context;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_navigation, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        name.setText(user.getFirstName() + " " + user.getLastName());
        username.setText(user.getUsername());
        email.setText(user.getEmail());
    }


    @OnClick(R.id.edit_email)
    public void onEditEmailClick() {
        navigation.fragmentEditEmail();
    }

    @OnClick(R.id.edit_password)
    public void onEditPasswordClick() {
        navigation.fragmentEditPassword();
    }

    @OnClick(R.id.edit_data)
    public void onEditName() {
        navigation.fragmentEditName();
    }

}