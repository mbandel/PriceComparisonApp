package com.pc.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pc.R;
import com.pc.activity.ProductActivity;
import com.pc.model.Category;
import com.pc.util.IconManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Activity activity;
    private List<Category> categories;

    public CategoryAdapter(Activity activity, List<Category> categories){
        this.activity = activity;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View categoryView = layoutInflater.inflate(R.layout.category_item, parent, false);
        return new CategoryAdapter.ViewHolder(categoryView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.name.setText(category.getName());
        holder.icon.setImageResource(IconManager.setIcon(category.getName()));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(activity.getApplicationContext(), ProductActivity.class);
            intent.putExtra("id", category.getId());
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }



    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.category_icon)
        ImageView icon;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
