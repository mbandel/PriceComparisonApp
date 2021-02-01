package com.pc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.pc.PriceComparison;
import com.pc.R;
import com.pc.adapter.ChooseListAdapter;
import com.pc.model.Poster;
import com.pc.model.ShoppingList;
import com.pc.model.Store;
import com.pc.util.NavigationAddPoster;
import com.pc.util.NavigationAddToList;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddToListFragment extends Fragment {

    @BindView(R.id.list_of_stores)
    RecyclerView recyclerView;
    private List<ShoppingList> shoppingLists;
    private Poster poster;
    private NavigationAddToList navigation;

    public AddToListFragment(List<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NavigationAddToList){
            navigation = (NavigationAddToList) context;
        }else {
            throw new RuntimeException(context.toString() + "NavigationAddPoster is not implemented");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_to_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ChooseListAdapter adapter = new ChooseListAdapter(shoppingLists, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @OnClick(R.id.add_btn)
    public void onAddButtonClick() {
        navigation.addToList();
    }
    @OnClick(R.id.exit_btn)
    public void onExitButtonClick() {
        navigation.exit();
    }
}

