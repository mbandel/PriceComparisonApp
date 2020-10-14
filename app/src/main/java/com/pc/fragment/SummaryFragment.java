package com.pc.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pc.R;
import com.pc.model.Poster;
import com.pc.util.NavigationAddPoster;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SummaryFragment extends Fragment {

    @BindView(R.id.product_value)
    TextView productValue;
    @BindView(R.id.price_value)
    TextView priceValue;
    @BindView(R.id.store_name_value)
    TextView storeNameValue;
    @BindView(R.id.store_address_value)
    TextView storeAddressValue;

    private Poster poster;
    private NavigationAddPoster navigation;

    public SummaryFragment(Poster poster) {
        this.poster = poster;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NavigationAddPoster){
            navigation = (NavigationAddPoster) context;
        }else {
            throw new RuntimeException(context.toString() + "NavigationAddPoster is not implemented");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        productValue.setText(poster.getProduct().getName());
        priceValue.setText(poster.getPrice().toString());
        storeNameValue.setText(poster.getStore().getName());
        storeAddressValue.append(poster.getStore().getAddress());
    }

    @OnClick(R.id.confirm_btn)
    public void onConfirmButtonClick() {
        setPosterData();
        navigation.addPoster(poster);
    }

    @OnClick(R.id.back_btn)
    public void onBackButtonClick() {
        navigation.goToStore();
    }

    private void setPosterData(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormat.format(date);
        poster.setDate(dateString);

    }
}