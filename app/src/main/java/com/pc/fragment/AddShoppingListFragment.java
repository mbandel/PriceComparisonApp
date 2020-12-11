package com.pc.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pc.R;
import com.pc.model.ShoppingList;
import com.pc.model.Store;
import com.pc.model.User;
import com.pc.util.NavigationAddShoppingList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddShoppingListFragment extends Fragment {

    @BindView(R.id.name_input)
    TextInputEditText nameInput;
    @BindView(R.id.find_store)
    AutoCompleteTextView findStore;
    @BindView(R.id.map_btn)
    MaterialButton mapButton;
    @BindView(R.id.add_btn)
    MaterialButton addButton;

    private List<Store> stores;
    private int userId;
    private NavigationAddShoppingList navigation;

    public AddShoppingListFragment(int userId, List<Store> stores) {
        this.userId = userId;
        this.stores = stores;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NavigationAddShoppingList) {
            navigation = (NavigationAddShoppingList) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_shopping_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.map_btn)
    public void onMapButtonClick() {
        navigation.goToMap();
    }

    @OnClick(R.id.add_btn)
    public void onAddButtonClick() {
        User user = new User(userId);
        String name = nameInput.getText().toString();
//        TODO dodac liste
//        ShoppingList shoppingList = new ShoppingList(name, getDate(),  user,  );
//        navigation.addShoppingList(shoppingList);
    }

    private String getDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
       return dateFormat.format(date);
    }
}