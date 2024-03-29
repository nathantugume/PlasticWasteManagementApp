package com.ugwebstudio.plasticwastemanagementapp.ui.collector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.android.gms.maps.model.LatLng;

import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.classes.GeocodingUtil;
import com.ugwebstudio.plasticwastemanagementapp.classes.Pickup;
import com.ugwebstudio.plasticwastemanagementapp.ui.LoginActivity;
import com.ugwebstudio.plasticwastemanagementapp.ui.customer.RedeemPointActivity;
import com.ugwebstudio.plasticwastemanagementapp.ui.customer.ScheduledPickupActivity;
import com.ugwebstudio.plasticwastemanagementapp.ui.customer.UserDashboardActivity;

import java.util.Objects;

public class CollectorDashboardActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference pickupsRef = db.collection("pickups");
    private Pickup pickup;
    private String userID;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private GoogleMap mMap;
    private double latitude;  // Fetched latitude from Firebase
    private double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector_dashboard);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        boolean isFirstLogin = sharedPreferences.getBoolean("isFirstLogin", true);
        userID = sharedPreferences.getString("UID","");

        if (isFirstLogin) {
            startActivity(new Intent(CollectorDashboardActivity.this, GetStartedCollectorActivity.class));
        }


        TextView collectionsCompletedTextView = findViewById(R.id.collections_completed_text_view);
        TextView totalWeightTextView = findViewById(R.id.total_weight_text_view);
        TextView upcomingCollectionsTextView = findViewById(R.id.upcoming_collections_text_view);

        // Find the TextViews inside the next pickup card
        TextView nextPickupTimeTextView = findViewById(R.id.next_pickup_time_text_view);
        TextView nextPickupLocationTextView = findViewById(R.id.next_pickup_location_text_view);

        MaterialCardView upcomingCard = findViewById(R.id.upcoming_collection_card);
        upcomingCard.setOnClickListener(view -> startActivity(new Intent(CollectorDashboardActivity.this, UpcomingCollectionsActivity.class)));


        // Fetch scheduled pickups and update TextViews
    fetchScheduledCompleted(collectionsCompletedTextView);
    fetchScheduledUpcoming(upcomingCollectionsTextView);
    fetchScheduledPending(nextPickupTimeTextView,nextPickupLocationTextView);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        topAppBar.setNavigationOnClickListener(view -> drawerLayout.open());

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.upcoming_pickup){
                drawerLayout.close();
                startActivity(new Intent(CollectorDashboardActivity.this, UpcomingCollectionsActivity.class));

            }

            if(item.getItemId() == R.id.collect){
                drawerLayout.close();
                startActivity(new Intent(CollectorDashboardActivity.this, CollectActivity.class));
            }
            if(item.getItemId() == R.id.reports){
                drawerLayout.close();
                startActivity(new Intent(CollectorDashboardActivity.this, ReportsActivity.class));
            }

            if (item.getItemId() == R.id.sign_out){
                drawerLayout.close();
                mAuth.getCurrentUser();
                mAuth.signOut();
                startActivity(new Intent(CollectorDashboardActivity.this, LoginActivity.class));


            }

            if (item.getItemId() == R.id.share){
                drawerLayout.close();
//                share();
            }
            return false;
        });

//initialise the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);



    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at the fetched coordinates and move the camera
        LatLng pickupLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 15)); // Zoom level 15
    }





    @SuppressLint("SetTextI18n")
    private void fetchScheduledCompleted(TextView collectionsCompletedTextView) {
        // Query pickups with status "completed" and assigned to the current collector
        Query query = pickupsRef.whereEqualTo("status", "completed")
                .whereEqualTo("collector", userID);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int completedCount = task.getResult().size();
                collectionsCompletedTextView.setText(String.valueOf(completedCount));
            }  // Handle unsuccessful fetch

        });
    }
    @SuppressLint("SetTextI18n")
    private void fetchScheduledPending(TextView nextPickupTimeTextView, TextView nextPickupLocationTextView) {
        // Query pickups with status "Pending" and assigned to the current collector
        pickupsRef
                .whereEqualTo("status", "Pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot pickupSnapshot = task.getResult().getDocuments().get(0);
                        String collector = pickupSnapshot.getString("collector");

                            String pickupDate = pickupSnapshot.getString("date");
                            String pickupTime = pickupSnapshot.getString("time");
                            String coordinates = pickupSnapshot.getString("pickupLocation");

                        String pickupLocation = coordinates;
                        String[] parts = pickupLocation.split(", ");
                        String splitLatitude = parts[0];
                        String splitLongitude = parts[1];

                        latitude = Double.parseDouble(splitLatitude);
                        longitude = Double.parseDouble(splitLongitude);

                        String textLocation = GeocodingUtil.getAddressFromCoordinates(this, latitude, longitude);


                        nextPickupTimeTextView.setText(pickupDate + ", " + pickupTime);
                            nextPickupLocationTextView.setText("Location: " + textLocation);

                    } else {
                        Log.e("fetchScheduledPending", "Error fetching pending pickups: " + task.getException());
                        nextPickupTimeTextView.setText("Next Pickup Time: N/A");
                        nextPickupLocationTextView.setText("Next Pickup Location: N/A");
                    }
                });
    }



    @SuppressLint("SetTextI18n")
    private void fetchScheduledUpcoming(TextView upcomingCollectionsTextView) {
        // Query pickups with status "completed" and assigned to the current collector
        Query query = pickupsRef.whereEqualTo("status", "scheduled");

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int upcomingCount = task.getResult().size();
                upcomingCollectionsTextView.setText(String.valueOf(upcomingCount));
            } else {
                // Handle unsuccessful fetch
            }
        });
    }



}