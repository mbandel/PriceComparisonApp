package com.pc.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pc.R;
import com.pc.activity.PosterDetailsActivity;
import com.pc.model.Poster;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.ViewHolder> {

    private Activity activity;
    private List<Poster> posters;

    public PosterAdapter(Activity activity, List<Poster> posters){
        this.activity = activity;
        this.posters = posters;
    }

    @NonNull
    @Override
    public PosterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View posterView = layoutInflater.inflate(R.layout.poster_item, parent, false);
        return new PosterAdapter.ViewHolder(posterView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Poster poster = posters.get(position);
        holder.price.setText(String.format("%.2f", poster.getPrice()) + " zł");
        holder.shop.setText(poster.getStore().getName());
        if (poster.getRatingValue() > 0)
            holder.rating.setTextColor(activity.getColor(R.color.colorGreen));
        else if (poster.getRatingValue() < 0)
            holder.rating.setTextColor(activity.getColor(R.color.colorRed));
        holder.rating.setText(String.valueOf(poster.getRatingValue()));
        holder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.getApplicationContext(), PosterDetailsActivity.class);
                intent.putExtra("id", poster.getId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posters.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.shop)
        TextView shop;

        @BindView(R.id.more_info)
        ImageView moreInfo;
        @BindView(R.id.rating)
        TextView rating;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
