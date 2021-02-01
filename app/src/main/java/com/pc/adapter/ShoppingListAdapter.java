package com.pc.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.pc.R;
import com.pc.model.Poster;
import com.pc.model.ShoppingList;
import com.pc.retrofit.Connector;
import com.pc.util.IconManager;
import com.pc.util.NavigationShoppingList;
import com.pc.util.SortPoster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private Activity activity;
    private List<Poster> posters;
    private List<Poster> postersWithoutDuplicates;
    Connector connector;
    private int shoppingListId;
    private SharedPreferences sharedPreferences;
    private NavigationShoppingList navigation;

    public ShoppingListAdapter(Activity activity, List<Poster> posters, int shoppingListId) {
        this.activity = activity;
        this.posters = SortPoster.sortByCategory(posters);
        this.postersWithoutDuplicates = removeDuplicates(this.posters);
        this.shoppingListId = shoppingListId;
        connector = Connector.getInstance();
        navigation = (NavigationShoppingList) activity;
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
        int amount = 0;
        HashMap<Poster, Integer> posterFreq = new HashMap<>();
        for(Poster poster1 : posters){
            amount = Collections.frequency(posters, poster1);
            posterFreq.put(poster1, amount);
        }
        Poster posterWithoutDuplicate = postersWithoutDuplicates.get(position);

        holder.name.setText(posterWithoutDuplicate.getProduct().getName());
        holder.price.setText(String.format(Locale.ENGLISH, "%.2f zł", posterWithoutDuplicate.getPrice()));
        holder.categoryIcon.setImageResource(IconManager.setIcon(posterWithoutDuplicate.getProduct().getCategory().getName()));
        holder.amount.setText(String.valueOf(posterFreq.get(posterWithoutDuplicate)));
        holder.delete.setOnClickListener(view -> {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.exit_dialog);
            TextView title = dialog.findViewById(R.id.title);
            title.setText("Czy na pewno chcesz usunąc produkt?");
            MaterialButton noButton = dialog.findViewById(R.id.no_btn);
            noButton.setOnClickListener(v -> {
                dialog.dismiss();
            });
            MaterialButton yesButton = dialog.findViewById(R.id.yes_btn);
            yesButton.setOnClickListener(v -> {
                dialog.dismiss();
                sharedPreferences = activity.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
                String token = sharedPreferences.getString("token", "");
                Call<String> removePosterCall = connector.serverApi.removePosterFromShoppingList(token, shoppingListId, posterWithoutDuplicate);
                removePosterCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            navigation.refresh();
                            Toast.makeText(activity.getApplicationContext(), "Pomyślnie usunięto produkt", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                });
            });
            dialog.show();
        });

    }

    @Override
    public int getItemCount() {
        if (posters != null) {
            return postersWithoutDuplicates.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public List<Poster> removeDuplicates(List<Poster> posters){
        List<Poster> listWithoutDuplicates = new ArrayList<Poster>();
        for(Poster poster : posters){
            if (!listWithoutDuplicates.contains(poster))
                listWithoutDuplicates.add(poster);
        }
        return listWithoutDuplicates;
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
        @BindView(R.id.delete)
        ImageView delete;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
