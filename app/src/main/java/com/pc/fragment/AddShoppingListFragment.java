package com.pc.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.pc.PriceComparison;
import com.pc.R;
import com.pc.model.ShoppingList;
import com.pc.model.Store;
import com.pc.model.User;
import com.pc.util.NavigationAddShoppingList;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddShoppingListFragment extends Fragment implements Validator.ValidationListener, TextWatcher {

    @BindView(R.id.constraint_layout)
    ConstraintLayout constraintLayout;
    @Length(min = 3, max = 30)
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
    private Validator validator;
    boolean isInputValid;
    private String[] storeNames;
    private SharedPreferences sharedPreferences;

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
        validator = new Validator(this);
        validator.setValidationListener(this);
        storeNames = new String[stores.size()];
        for (int i=0; i<stores.size(); i++){
            storeNames[i] = stores.get(i).getName() + " " + stores.get(i).getAddress();
        }
        setArrayAdapter(storeNames, findStore);

        sharedPreferences = getActivity().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);

    }

    @Override
    public void onResume() {
        super.onResume();
        setStoreTextView();
    }

    @OnClick(R.id.map_btn)
    public void onMapButtonClick() {
        navigation.goToMap();
    }

    @OnClick(R.id.add_btn)
    public void onAddButtonClick() {
        validator.validate();
        if (isInputValid && validateStore()) {
            User user = new User(userId);
            String name = nameInput.getText().toString();
            ShoppingList shoppingList = new ShoppingList(name, getDate(), getStore(), user);
            navigation.addShoppingList(shoppingList);
        }
    }

    private String getDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
       return dateFormat.format(date);
    }

    @Override
    public void onValidationSucceeded() {
        isInputValid = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        isInputValid = false;
        for (ValidationError error : errors){
            View view = error.getView();
            if (view == nameInput) {
                ((EditText) view).setError(getResources().getString(R.string.shopping_list_error));
            } else ((EditText) view).setError(null);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        validator.validate();
    }

    private void setArrayAdapter(String [] list, AutoCompleteTextView actv){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.product_list, R.id.text_view_list_item, list);
        actv.setAdapter(adapter);
    }

    private boolean validateStore() {
        List<String> storeList = Arrays.asList(storeNames);
        if(storeList.contains(findStore.getText().toString())){
            return true;
        } else {
            PriceComparison.createSnackbar(constraintLayout, getContext().getString(R.string.wrong_store_name)).show();
            return false;
        }
    }

    private Store getStore() {
        for (Store store : stores) {
            if ((store.getName() + " " + store.getAddress()).equals(findStore.getText().toString())){
                return store;
            }
        }
        return null;
    }

    private void setStoreTextView() {
        if (sharedPreferences.getBoolean("setStore", false)){
            String storeName = sharedPreferences.getString("storeName", " ");
            findStore.setText(storeName);
            sharedPreferences.edit()
                    .putBoolean("setStore", false)
                    .apply();

        }
    }
}