package com.pc.adapter;

import android.app.Activity;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pc.R;
import com.pc.model.Poster;

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
        holder.price.setText(poster.getPrice() + "zł");
        holder.shop.setText(poster.getStore().getName());
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
        @BindView(R.id.like)
        ImageView like;
        @BindView(R.id.dislike)
        ImageView dislike;
        @BindView(R.id.more_info)
        ImageView moreInfo;

        public ViewHolder(@NonNull android.view.View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
