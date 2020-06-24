package com.pc.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pc.R;
import com.pc.model.Category;

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
        holder.icon.setImageResource(setIcon(category.getName()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("id " + category.getId() + "name: "+  category.getName());
            }
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

    public int setIcon(String iconType){
        if (iconType.equals("słodycze"))
            return R.drawable.ic_candy;
        else if (iconType.equals("pieczywo"))
            return R.drawable.ic_bread;
        else if (iconType.equals("napoje"))
            return R.drawable.ic_drinks;
        else if (iconType.equals("warzywa"))
            return R.drawable.ic_vegetable;
        else if (iconType.equals("owoce"))
            return R.drawable.ic_fruit;
        else if (iconType.equals("przyprawy"))
            return R.drawable.spices;
        else if (iconType.equals("alkohole"))
            return R.drawable.ic_alcohol;
        else if (iconType.equals("nabiał"))
            return R.drawable.ic_milk;
        else if (iconType.equals("mięso"))
            return R.drawable.ic_meat;
        else if (iconType.equals("ryby"))
            return R.drawable.ic_fish;
        else return R.drawable.border_bg;
    }
}
