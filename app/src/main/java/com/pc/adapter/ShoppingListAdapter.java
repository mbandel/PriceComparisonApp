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
import com.pc.util.IconManager;
import com.pc.util.SortPoster;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private Activity activity;
    private List<Poster> posters;

    public ShoppingListAdapter(Activity activity, List<Poster> posters) {
        this.activity = activity;
        this.posters = SortPoster.sortByCategory(posters);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.shopping_list_item, parent, false);
        return new ShoppingListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Poster poster = posters.get(position);
        holder.name.setText(poster.getProduct().getName());
        holder.price.setText(String.format(Locale.ENGLISH, "%.2f zł", poster.getPrice()));
        holder.categoryIcon.setImageResource(IconManager.setIcon(poster.getProduct().getCategory().getName()));
    }

    @Override
    public int getItemCount() {
        if (posters != null) {
            return posters.size();
        } else {
            return 0;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.amount)
        TextView amount;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.category_icon)
        ImageView categoryIcon;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
