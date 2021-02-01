package com.pc.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pc.R;
import com.pc.model.Poster;
import com.pc.model.ShoppingList;
import com.pc.util.NavigationAddToList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

public class ChooseListAdapter extends RecyclerView.Adapter<ChooseListAdapter.ViewHolder> {

    private List<ShoppingList> shoppingLists;
    private Activity activity;
    private NavigationAddToList navigation;

    public ChooseListAdapter(List<ShoppingList> shoppingLists, Activity activity) {
        this.shoppingLists = shoppingLists;
        this.activity = activity;
        navigation = (NavigationAddToList) activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.choose_list_item, parent, false);
        return new ChooseListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingList shoppingList = shoppingLists.get(position);
        holder.name.setText(shoppingList.getName());
        holder.name.setOnClickListener(view -> {
            holder.name.setBackground(activity.getDrawable(R.drawable.orange_border));
            navigation.selectList(shoppingLists.get(position));
        });
    }

    @Override
    public int getItemCount() {
        if (shoppingLists != null ) {
            return shoppingLists.size();
        }
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.name)
        TextView name;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
