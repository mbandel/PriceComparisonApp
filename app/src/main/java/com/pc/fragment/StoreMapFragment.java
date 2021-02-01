package com.pc.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidmapsextensions.ClusteringSettings;

import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.SupportMapFragment;


import com.androidmapsextensions.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.material.button.MaterialButton;
import com.pc.R;
import com.pc.model.Poster;
import com.pc.model.Store;
import com.pc.util.NavigationAddPoster;
import com.pc.util.NavigationAddShoppingList;
import com.pc.util.NavigationType;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pc.util.NavigationType.*;

public class StoreMapFragment extends Fragment implements OnMapReadyCallback{

    @BindView(R.id.info_close)
    ImageView infoClose;
    @BindView(R.id.info_panel)
    ConstraintLayout markerInfo;
    @BindView(R.id.store_address_value)
    TextView storeAddress;
    @BindView(R.id.store_name_value)
    TextView storeName;
    @BindView(R.id.progress_layout)
    ConstraintLayout progressLayout;
    @BindView(R.id.select_btn)
    MaterialButton selectButton;

    private List<Store> stores;
    private Poster poster;

    private NavigationAddShoppingList navigationAddShoppingList;
    private NavigationAddPoster navigationAddPoster;
    private NavigationType navigationType;

    public StoreMapFragment(List<Store> stores, Poster poster){
        this.stores = stores;
        this.poster = poster;
    }

    public StoreMapFragment(List<Store> stores){
        this.stores = stores;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NavigationAddPoster){
            navigationAddPoster = (NavigationAddPoster) context;
            navigationType = ADD_POSTER;
        } else if (context instanceof NavigationAddShoppingList) {
            navigationAddShoppingList = (NavigationAddShoppingList) context;
            navigationType = ADD_SHOPPING_LIST;
        } else {
            throw new RuntimeException(context.toString() + "Navigation interface is not implemented");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        LatLng lodz = new LatLng(51.759445, 19.457216);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lodz, 12));
        ClusteringSettings clusteringSettings = new ClusteringSettings().addMarkersDynamically(true).clusterSize(52);
        googleMap.setClustering(clusteringSettings);

        for (Store store : stores) {
            LatLng location = getLocationFromAddress(store.getAddress());
            int icon = setIcon(store);
            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(icon))
                    .position(location))
                    .setData(store);
        }


        googleMap.setOnMarkerClickListener(marker -> {
            if(!marker.isCluster()){
                Store store = marker.getData();
                markerInfo.setVisibility(View.VISIBLE);
                storeName.setText(store.getName());
                storeAddress.setText(store.getAddress());
                selectButton.setOnClickListener(view -> {
                   switch (navigationType) {
                       case ADD_POSTER:
                           poster.setStore(store);
                           navigationAddPoster.goToSummary();
                           break;
                       case ADD_SHOPPING_LIST:
                           navigationAddShoppingList.goToEdition(store);
                           break;
                   }
                });
            }
            return false;
        });
        progressLayout.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.info_close)
    public void onInfoCloseClick() {
        markerInfo.setVisibility(View.GONE);
    }

    public int setIcon(Store store){
        switch (store.getName()){
            case "Lidl":
                return R.drawable.lidl;
            case "Kaufland":
                return R.drawable.kaufland;
            default:
                return R.drawable.biedronka;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        progressLayout.setVisibility(View.VISIBLE);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getExtendedMapAsync(this);
        }
    }

    public LatLng getLocationFromAddress(String strAddress){
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        LatLng latLng = null;
        try {
            address = coder.getFromLocationName(strAddress,1);
            if (address == null) {

                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }catch (IOException e){
            e.printStackTrace();
        }
        return latLng;
    }
}