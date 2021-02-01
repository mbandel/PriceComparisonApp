package com.pc.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.app.Activity;
import android.content.Context;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.SupportMapFragment;


import com.androidmapsextensions.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.material.button.MaterialButton;
import com.pc.R;
import com.pc.activity.PosterDetailsActivity;
import com.pc.model.Poster;
import com.pc.model.Store;
import com.pc.util.MapStateManager;
import com.pc.util.NavigationAddPoster;
import com.pc.util.NavigationEditPosterList;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import timber.log.Timber;

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
   private NavigationEditPosterList navigation;

    public static MapsFragment newInstance(List<Poster> posters){
        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.posters = posters;
        return mapsFragment;
    }

    private MapsFragment(){ }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        System.out.println("MapsFragment OnAttach()");
        if (context instanceof NavigationEditPosterList){
            navigation = (NavigationEditPosterList) context;
        }else {
            throw new RuntimeException(context.toString() + "NavigationEditPosterList is not implemented");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {

        super.onPause();
    }


    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onMapReady(GoogleMap google){
        googleMap = google;
        LatLng lodz = new LatLng(51.759446, 19.457217);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lodz, 12));
        ClusteringSettings clusteringSettings = new ClusteringSettings().addMarkersDynamically(false).clusterSize(52);
        googleMap.setClustering(clusteringSettings);


//        MapStateManager mgr = new MapStateManager(getContext());
//        CameraPosition position = mgr.getSavedCameraPosition();
//        if (position != null) {
//            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
//            googleMap.moveCamera(update);
//            googleMap.setMapType(mgr.getSavedMapType());
//        }else {

//        }
        for (Poster poster: posters) {
            LatLng location = getLocationFromAddress(poster.getStore().getAddress());
            int icon = setIcon(poster);
            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(icon))
                    .position(location))
                    .setData(poster);
        }


        googleMap.setOnMarkerClickListener(marker -> {
            if(!marker.isCluster()){
                Poster poster = marker.getData();
                markerInfo.setVisibility(View.VISIBLE);
                storeName.setText(poster.getStore().getName());
                storeAddress.setText(poster.getStore().getAddress());
                price.setText(String.format(Locale.ENGLISH, "%.2f zł", poster.getPrice()));
                rating.setText(String.valueOf(poster.getRatingValue()));
                selectButton.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), PosterDetailsActivity.class);
                    intent.putExtra("id", poster.getId());
                    getActivity().startActivity(intent);
                });
            }
            return false;
        });

        searchView.setOnQueryTextListener(this);
        progressLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDetach() {
//        List<Poster> newPosterList = new ArrayList<>();
//        for (int i = 0; i < googleMap.getDisplayedMarkers().size(); i++){
//            newPosterList.add(googleMap.getDisplayedMarkers().get(i).getData());
//        }
//        Timber.e("New poster list size: " + newPosterList.size());
//        navigation.editPosterList(newPosterList);

        super.onDetach();
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
                return new LatLng(0, 0);
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