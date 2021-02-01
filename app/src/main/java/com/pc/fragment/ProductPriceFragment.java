package com.pc.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.pc.R;
import com.pc.model.Poster;
import com.pc.model.Product;
import com.pc.util.NavigationAddPoster;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductPriceFragment extends Fragment {

    @BindView(R.id.next_btn)
    MaterialButton nextButton;
    @BindView(R.id.find_product)
    AutoCompleteTextView findProduct;
    @BindView(R.id.price)
    EditText priceEditText;

    private NavigationAddPoster navigation;
    private List<Product> products;
    private String[] productNames;
    private Poster poster;
    private double price;

    public ProductPriceFragment(List<Product> products, Poster poster) {
        this.products = products;
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
        View view = inflater.inflate(R.layout.fragment_product_price, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        productNames = new String[products.size()];
        for (int i = 0; i < products.size(); i++) {
            productNames[i] = products.get(i).getName();
        }
        setArrayAdapter(productNames, findProduct);
    }

    private void setArrayAdapter(String [] list, AutoCompleteTextView actv){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.product_list, R.id.text_view_list_item, list);
        actv.setAdapter(adapter);
    }

    @OnClick(R.id.next_btn)
    public void onNextButtonClick(){
        if (validatePrice() && validateProduct()){
            setPosterData();
            navigation.goToStore();
        }

    }

    private boolean validateProduct(){
        if (findProduct.getText().toString().equals("")){
            Toast.makeText(getContext(), "Niepoprawna nazwa produktu", Toast.LENGTH_SHORT).show();
            return false;
        }
        List<String> productNameList  = Arrays.asList(productNames);
        if(productNameList.contains(findProduct.getText().toString())){
            return true;
        }
        else {
            Toast.makeText(getContext(), "Niepoprawna nazwa produktu", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean validatePrice() {
        if (priceEditText.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Niepoprawna cena", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setPosterData(){
        double price = Double.parseDouble(priceEditText.getText().toString());
        poster.setPrice(price);
        for (Product product : products){
            if (product.getName().equals(findProduct.getText().toString())){
                poster.setProduct(product);
            }
        }

    }

}