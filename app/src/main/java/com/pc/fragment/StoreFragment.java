package com.pc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.pc.PriceComparison;
import com.pc.R;
import com.pc.model.Poster;
import com.pc.model.Store;
import com.pc.util.NavigationAddPoster;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoreFragment extends Fragment {

    @BindView(R.id.constraint_layout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.next_btn)
    MaterialButton nextButton;
    @BindView(R.id.find_store)
    AutoCompleteTextView findStore;

    NavigationAddPoster navigation;
    private List<Store> stores;
    private String[] storeNames;
    private Poster poster;

    public StoreFragment(List<Store> stores, Poster poster) {
        this.stores = stores;
        this.poster = poster;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NavigationAddPoster){
            navigation = (NavigationAddPoster) context;
        }else {
            throw new RuntimeException(context.toString() + "NavigationAddPoster is not implemented");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        storeNames = new String[stores.size()];
        for (int i=0; i<stores.size(); i++){
            storeNames[i] = stores.get(i).getName() + " " + stores.get(i).getAddress();
        }
        setArrayAdapter(storeNames, findStore);
    }

    private void setArrayAdapter(String [] list, AutoCompleteTextView actv){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.product_list, R.id.text_view_list_item, list);
        actv.setAdapter(adapter);
    }


    @OnClick(R.id.map_btn)
    public void onMapButtonClick(){
        navigation.goToMap();
    }

    @OnClick(R.id.next_btn)
    public void onNextButtonClick(){
        if(validateStore()){
            setStoreData();
            navigation.goToSummary();
        }
    }

    @OnClick(R.id.back_btn)
    public void onBackButtonClick(){
        navigation.goToProduct();
    }

    private boolean validateStore(){
        List<String> storeList = Arrays.asList(storeNames);
        if(storeList.contains(findStore.getText().toString())){
            return true;
        } else {
            PriceComparison.createSnackbar(constraintLayout, getContext().getString(R.string.wrong_store_name)).show();
            return false;
        }
    }

    private void setStoreData(){
        for (Store store : stores){
            if ((store.getName() + " " + store.getAddress()).equals(findStore.getText().toString())){
                poster.setStore(store);
            }
        }
    }
}

