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
import com.pc.activity.PosterActivity;
import com.pc.model.Category;
import com.pc.model.Product;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Activity activity;
    private List<Product> products;

    public ProductAdapter(Activity activity, List<Product> products){
        this.activity = activity;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View productView = layoutInflater.inflate(R.layout.product_item, parent, false);
        return new ProductAdapter.ViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.name.setText(product.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.getApplicationContext(), PosterActivity.class);
                intent.putExtra("id", product.getId());
                intent.putExtra("name", product.getName());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (products != null)
            return products.size();
        else
            return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name)
        TextView name;


        public ViewHolder(@NonNull android.view.View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
