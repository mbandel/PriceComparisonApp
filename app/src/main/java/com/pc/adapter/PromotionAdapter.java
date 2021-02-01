package com.pc.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pc.R;
import com.pc.activity.PosterDetailsActivity;
import com.pc.activity.ProductActivity;
import com.pc.model.Category;
import com.pc.model.Poster;
import com.pc.util.IconManager;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.ViewHolder> {
    private Activity activity;
    private List<Poster> promotions;

    public PromotionAdapter(Activity activity, List<Poster> promotions){
        this.activity = activity;
        this.promotions = promotions;
    }

    @NonNull
    @Override
    public PromotionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.promotion_item, parent, false);
        return new PromotionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionAdapter.ViewHolder holder, int position) {
        Poster poster = promotions.get(position);
        holder.name.setText(poster.getProduct().getName());
        holder.icon.setImageResource(IconManager.setIcon(poster.getProduct().getCategory().getName()));
        holder.store.setText(poster.getStore().getName());
        holder.oldPrice.setText(String.format(Locale.ENGLISH, "%.2f zł", poster.getPrice()));
        holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.newPrice.setText(String.format(Locale.ENGLISH, "%.2f zł", poster.getPromotionPrice()));
        holder.date.setText(poster.getPromotionDate());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(activity.getApplicationContext(), PosterDetailsActivity.class);
            intent.putExtra("id", poster.getId());
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return promotions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.category_icon)
        ImageView icon;
        @BindView(R.id.store_value)
        TextView store;
        @BindView(R.id.old_price)
        TextView oldPrice;
        @BindView(R.id.new_price)
        TextView newPrice;
        @BindView(R.id.date_value)
        TextView date;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
