package com.example.dogwalker.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.dogwalker.R;
import com.example.dogwalker.models.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class AddressSelectionDialog extends DialogFragment implements OnMapReadyCallback {

    EditText addressEt;

    Button addressSubmitBtn, addressCloseBtn;
    GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    LatLng latLng;
    OnSuccessListener<Address> addressDelegate;

    public AddressSelectionDialog(OnSuccessListener<Address> addressDelegate) {
        this.addressDelegate = addressDelegate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.address_select, container, false);
        addressEt = rootView.findViewById(R.id.address_select_et);
        addressSubmitBtn = rootView.findViewById(R.id.addressSubmitBtn);
        addressCloseBtn = rootView.findViewById(R.id.addressCloseBtn);

        addressCloseBtn.setOnClickListener(view -> dismiss());
        addressSubmitBtn.setOnClickListener(view -> {
            if (addressEt.getText().toString().isEmpty() || latLng == null) {
                Toast.makeText(getContext(), "Please enter address", Toast.LENGTH_SHORT).show();
            } else {
                addressDelegate.onSuccess(new Address(addressEt.getText().toString(), String.valueOf(latLng.latitude), String.valueOf(latLng.longitude)));
                dismiss();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_address_select);
        mapFragment.getMapAsync(this);
        return rootView;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapClickListener(latLng -> {
                    this.latLng = latLng;
                    mMap.addMarker(new MarkerOptions().position(latLng).title("My Selected Address"));
                }
        );
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                centreMapOnLocation(location, "My Current Location");
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        centreMapOnLocation(lastKnownLocation, "Your Location");
        mMap.setMyLocationEnabled(true);
    }

    public void centreMapOnLocation(Location location, String title) {
        if (location == null) return;
        this.latLng = new LatLng(location.getLatitude(),location.getLongitude());
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
    }
}
