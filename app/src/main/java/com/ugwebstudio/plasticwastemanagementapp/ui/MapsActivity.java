package com.ugwebstudio.plasticwastemanagementapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ugwebstudio.plasticwastemanagementapp.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int REQUEST_CODE_MAP = 101;
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set the default area to Fort Portal, Uganda
        LatLngBounds FORT_PORTAL_BOUNDS = new LatLngBounds(
                new LatLng(0.6510, 30.2550),      // Southwest bound (slightly south-west of Fort Portal)
                new LatLng(0.6910, 30.2950)       // Northeast bound (slightly north-east of Fort Portal)
        );


        // Move the camera to Uganda and set an appropriate zoom level
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(FORT_PORTAL_BOUNDS, 0));

        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Set map click listener to allow users to select location
        mMap.setOnMapClickListener(latLng -> {
            // Clear previous marker
            mMap.clear();

            // Add marker at selected location
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            marker.showInfoWindow();

            // Pass selected location back to the calling activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_LATITUDE, latLng.latitude);
            resultIntent.putExtra(EXTRA_LONGITUDE, latLng.longitude);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
