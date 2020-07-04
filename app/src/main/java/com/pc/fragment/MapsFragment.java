package com.pc.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidmapsextensions.ClusteringSettings;

import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.SupportMapFragment;


import com.androidmapsextensions.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.pc.R;
import com.pc.model.Poster;
import com.pc.model.Store;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsFragment extends Fragment implements OnMapReadyCallback{

    @BindView(R.id.info_close)
    ImageView infoClose;
    @BindView(R.id.info_panel)
    ConstraintLayout markerInfo;
    @BindView(R.id.store_address_value)
    TextView storeAddress;
    @BindView(R.id.store_name_value)
    TextView storeName;
    @BindView(R.id.price_value)
    TextView price;
    @BindView(R.id.rating_value)
    TextView rating;

   private List<Poster> posters;

    public static MapsFragment newInstance(List<Poster> posters){
        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.posters = posters;
        return mapsFragment;
    }

    private MapsFragment(){ }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        ClusteringSettings clusteringSettings = new ClusteringSettings().addMarkersDynamically(true).clusterSize(52);
        googleMap.setClustering(clusteringSettings);

        for (Poster poster: posters) {
            LatLng location = getLocationFromAddress(poster.getStore().getAddress());
            int icon = setIcon(poster);
            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(icon))
                    .position(location))
                    .setData(poster);
        }

        LatLng lodz = new LatLng(51.759445, 19.457216);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lodz, 12));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.isCluster()){
                    Poster poster = marker.getData();
                    markerInfo.setVisibility(View.VISIBLE);
                    storeName.setText(poster.getStore().getName());
                    storeAddress.setText(poster.getStore().getAddress());
                    price.setText(poster.getPrice().toString() + " zł");
                    rating.setText(0+"");
                }
                return false;
            }
        });

    }

    @OnClick(R.id.info_close)
    public void onInfoCloseClick() {
        markerInfo.setVisibility(View.GONE);
    }

    public int setIcon(Poster poster){
        switch (poster.getStore().getName()){
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
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
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
            if (address==null) {
                System.out.println("address null");
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();
            latLng = new LatLng((double) (location.getLatitude()), (double) (location.getLongitude()));
        }catch (IOException e){
            e.printStackTrace();
        }
        return latLng;
    }
}