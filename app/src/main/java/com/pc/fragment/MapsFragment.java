package com.pc.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidmapsextensions.ClusteringSettings;

import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.SupportMapFragment;


import com.androidmapsextensions.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.material.button.MaterialButton;
import com.pc.R;
import com.pc.activity.PosterDetailsActivity;
import com.pc.model.Poster;
import com.pc.model.Store;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MapsFragment extends Fragment implements OnMapReadyCallback, SearchView.OnQueryTextListener {

    @BindView(R.id.search_edit_text)
    SearchView searchView;
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
    @BindView(R.id.progress_layout)
    ConstraintLayout progressLayout;
    @BindView(R.id.select_btn)
    MaterialButton selectButton;

   private List<Poster> posters;
   private GoogleMap googleMap;

    public static MapsFragment newInstance(List<Poster> posters){
        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.posters = posters;
        return mapsFragment;
    }

    private MapsFragment(){ }


    @Override
    public void onMapReady(GoogleMap google){
        googleMap = google;
        ClusteringSettings clusteringSettings = new ClusteringSettings().addMarkersDynamically(true).clusterSize(52);
        googleMap.setClustering(clusteringSettings);
        searchView.setOnQueryTextListener(this);

        for (Poster poster: posters) {
            LatLng location = getLocationFromAddress(poster.getStore().getAddress());
            int icon = setIcon(poster);
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(icon))
                    .position(location)
                    .title(poster.getPrice().toString()));
            marker.setData(poster);
            marker.showInfoWindow();
        }

        LatLng lodz = new LatLng(51.759445, 19.457216);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lodz, 12));

        googleMap.setOnMarkerClickListener(marker -> {
            if(!marker.isCluster()){
                Poster poster = marker.getData();
                markerInfo.setVisibility(View.VISIBLE);
                storeName.setText(poster.getStore().getName());
                storeAddress.setText(poster.getStore().getAddress());
                price.setText(String.format("%.2f", poster.getPrice()) + " zł");
                rating.setText(String.valueOf(poster.getRatingValue()));
                selectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), PosterDetailsActivity.class);
                        intent.putExtra("id", poster.getId());
                        getActivity().startActivity(intent);
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

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        LatLng latLng = getLocationFromAddress(searchView.getQuery().toString());
        if (latLng != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        } else {
            Toast.makeText(getActivity(), "Podaj poprawny adres", Toast.LENGTH_SHORT).show();
        }
        return false;
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
            if (address.isEmpty()) {
                System.out.println("address null");
                return null;
            }else {
                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();
                latLng = new LatLng((double) (location.getLatitude()), (double) (location.getLongitude()));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return latLng;
    }
}