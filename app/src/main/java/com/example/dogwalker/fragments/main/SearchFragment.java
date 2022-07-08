package com.example.dogwalker.fragments.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dogwalker.R;
import com.example.dogwalker.firebase.FirebaseManager;
import com.example.dogwalker.fragments.BaseFragment;
import com.example.dogwalker.models.Address;
import com.example.dogwalker.models.DogOwner;
import com.example.dogwalker.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment {
    List<User> usersAround;
    LocationManager locationManager;
    LocationListener locationListener;
    GoogleMap googleMap;

    @SuppressLint("MissingPermission")
    private OnMapReadyCallback callback = googleMap -> {
        this.googleMap = googleMap;
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                centreMapOnLocation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        googleMap.setOnMarkerClickListener(marker -> {
            User user = (User) marker.getTag();
            if (user == null) return false;
            Bundle b = new Bundle();
            b.putString("userId", user.getId());
            b.putBoolean("dogOwner", user instanceof DogOwner);
            navigate(R.id.action_searchFragment_to_userDetailsFragment, b);
            return true;
        });
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        centreMapOnLocation(lastKnownLocation);
        if (FirebaseManager.isDogWalker()) {
            FirebaseManager.addListenerToDogOwners(dogOwners -> {
                usersAround = new ArrayList<>();
                usersAround.addAll(dogOwners);
                googleMap.clear();
                for (User user : usersAround) {
                    Marker m = googleMap.addMarker(new MarkerOptions().position(new LatLng(
                            Double.parseDouble(user.getAddress().getLatitude()),
                            Double.parseDouble(user.getAddress().getLongtitude())
                    )).title("Dog owner name: " + user.getFullName() + ", Address: " + user.getAddress().getName()));
                    m.setTag(user);
                }
            }, e -> showToast("There was a problem loading close dog owners"));
        } else {
            FirebaseManager.addListenerToDogWalkers(dogWalkers -> {
                usersAround = new ArrayList<>();
                usersAround.addAll(dogWalkers);
                googleMap.clear();
                for (User user : usersAround) {
                    Marker m = googleMap.addMarker(new MarkerOptions().position(new LatLng(
                            Double.parseDouble(user.getAddress().getLatitude()),
                            Double.parseDouble(user.getAddress().getLongtitude())
                    )).title("Dog walker name: " + user.getFullName() + ", Address: " + user.getAddress().getName()));
                    m.setTag(user);
                }
            }, e -> showToast("There was a problem loading close dog walkers"));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void centreMapOnLocation(Location location) {
        if (location == null || googleMap == null) return;
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
    }
}