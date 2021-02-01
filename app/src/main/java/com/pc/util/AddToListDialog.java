package com.pc.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pc.R;
import com.pc.adapter.ChooseListAdapter;
import com.pc.model.ShoppingList;

import java.util.List;

public class AddToListDialog extends Dialog {

    public AddToListDialog(Context context, Activity activity, List<ShoppingList> shoppingLists) {
        super(context);
        setContentView(R.layout.fragment_add_to_list);
        RecyclerView storesRecyclerView = findViewById(R.id.list_of_stores);
        ChooseListAdapter adapter = new ChooseListAdapter(shoppingLists, activity);
        storesRecyclerView.setAdapter(adapter);
        storesRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }
}
