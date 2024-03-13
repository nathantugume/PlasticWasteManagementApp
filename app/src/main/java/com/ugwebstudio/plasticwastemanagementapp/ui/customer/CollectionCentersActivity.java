package com.ugwebstudio.plasticwastemanagementapp.ui.customer;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ugwebstudio.plasticwastemanagementapp.R;

public class CollectionCentersActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_centers);

        db = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Retrieve collection center data from Firestore
        db.collection("collectors")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Extract collection center data
                            String name = document.getString("businessName");
                            String locationString = document.getString("location");
                            LatLng location = parseLocation(locationString);

                            // Add marker for each collection center on the map
                            if (location != null) {
                                mMap.addMarker(new MarkerOptions().position(location).title(name));
                            }
                        }
                    } else {
                        Toast.makeText(CollectionCentersActivity.this, "Failed to retrieve collection centers", Toast.LENGTH_SHORT).show();
                    }
                });

        // Move camera to a default location
//        LatLng defaultLocation = new LatLng(0, 0); // Set default location
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10)); // Zoom level 10

        // Set the default area to Uganda
        LatLngBounds UGANDA_BOUNDS = new LatLngBounds(
                new LatLng(-1.4783, 29.4241),      // Southwest bound
                new LatLng(4.2330, 35.0007)       // Northeast bound
        );

        // Move the camera to Uganda and set an appropriate zoom level
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(UGANDA_BOUNDS, 0));

        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private LatLng parseLocation(String locationString) {
        try {
            String[] parts = locationString.split(",");
            double latitude = Double.parseDouble(parts[0]);
            double longitude = Double.parseDouble(parts[1]);
            return new LatLng(latitude, longitude);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }
}

