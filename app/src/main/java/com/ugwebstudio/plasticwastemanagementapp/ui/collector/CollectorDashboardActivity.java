package com.ugwebstudio.plasticwastemanagementapp.ui.collector;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.classes.GeocodingUtil;
import com.ugwebstudio.plasticwastemanagementapp.model.CollectorDashboardViewModel;
import com.ugwebstudio.plasticwastemanagementapp.ui.LoginActivity;

public class CollectorDashboardActivity extends AppCompatActivity implements OnMapReadyCallback {

    private CollectorDashboardViewModel viewModel;
    private String userID;
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String currentPickupId;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector_dashboard);
        viewModel = new ViewModelProvider(this).get(CollectorDashboardViewModel.class);

        TextView textViewPickupWeight = findViewById(R.id.total_weight_text_view);  // Assuming you have this TextView
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        topAppBar.setNavigationOnClickListener(view -> drawerLayout.open());

        TextView collectionsCompletedTextView = findViewById(R.id.collections_completed_text_view);
        TextView upcomingCollectionsTextView = findViewById(R.id.upcoming_collections_text_view);
        TextView nextPickupTimeTextView = findViewById(R.id.next_pickup_time_text_view);
        TextView nextPickupLocationTextView = findViewById(R.id.next_pickup_location_text_view);
        Button btn_completed = findViewById(R.id.btn_complete);

        MaterialCardView upcomingCard = findViewById(R.id.upcoming_collection_card);
        upcomingCard.setOnClickListener(view -> startActivity(new Intent(CollectorDashboardActivity.this, UpcomingCollectionsActivity.class)));

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        boolean isFirstLogin = sharedPreferences.getBoolean("isFirstLogin", true);
        userID = sharedPreferences.getString("UID","");

        if (isFirstLogin) {
            startActivity(new Intent(CollectorDashboardActivity.this, GetStartedCollectorActivity.class));
        }

        viewModel.getCompletedCount().observe(this, count -> collectionsCompletedTextView.setText(String.valueOf(count)));
        viewModel.getUpcomingCount().observe(this, count -> upcomingCollectionsTextView.setText(String.valueOf(count)));
        viewModel.getNextPickup().observe(this, pickupDetails -> {
            if (pickupDetails != null) {

                currentPickupId = pickupDetails.pickUpId;
                String coordinates = pickupDetails.location;

                String[] parts = coordinates.split(", ");
                String splitLatitude = parts[0];
                String splitLongitude = parts[1];

                latitude = Double.parseDouble(splitLatitude);
                longitude = Double.parseDouble(splitLongitude);

                String textLocation = GeocodingUtil.getAddressFromCoordinates(this, latitude, longitude);

                nextPickupTimeTextView.setText(pickupDetails.date + ", " + pickupDetails.time);
                nextPickupLocationTextView.setText(textLocation);
            } else {
                nextPickupTimeTextView.setText("Next Pickup Time: N/A");
                nextPickupLocationTextView.setText("Next Pickup Location: N/A");
            }
        });
        // Observe the LiveData for current pickup ID and weight
        viewModel.getCurrentPickupIdLiveData().observe(this, pickupId -> {
            if (pickupId != null && !pickupId.isEmpty()) {
                btn_completed.setOnClickListener(view -> showCompleteDialog(pickupId));
            } else {
                btn_completed.setOnClickListener(view ->
                        Toast.makeText(this, "No pickup selected!", Toast.LENGTH_SHORT).show());
            }
        });


        // In your activity or fragment
        viewModel.getTotalCompletedWeightLiveData().observe(this, totalWeight -> {
            if (totalWeight != null) {
                textViewPickupWeight.setText(totalWeight+" kgs");
            } else {
                textViewPickupWeight.setText("please try again!");
            }
        });


        // Now call ViewModel methods instead of directly querying in the activity
        viewModel.fetchCompletedPickups(userID);
        viewModel.fetchUpcomingPickups(userID);
        viewModel.fetchNextPickup(userID);
        viewModel.fetchCompletedPickupsTotalWeight(userID);
        //initialise the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);

        btn_completed.setOnClickListener(
                view -> {
                    if(!(currentPickupId == null)){
                        showCompleteDialog(currentPickupId);
                    }
                    else {
                        Toast.makeText(this, "There are no active pickups!!", Toast.LENGTH_SHORT).show();
                    }
                });

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


        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_nav_collections){
                startActivity(new Intent(CollectorDashboardActivity.this, UpcomingCollectionsActivity.class));
                 return true;
            }
            if (item.getItemId() == R.id.bottom_nav_pickups){
                startActivity(new Intent(CollectorDashboardActivity.this, UpcomingCollectionsActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.bottom_nav_reports){
                startActivity(new Intent(CollectorDashboardActivity.this, ReportsActivity.class));
                return true;
            }
            return false;
        });

    }


    private void showCompleteDialog(String pickupId) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_complete_pickup, null);
        TextInputEditText editTextWeight = dialogView.findViewById(R.id.edit_text_weight);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Complete Pickup")
                .setMessage("Enter the weight of collected items:")
                .setPositiveButton("Save", (dialog, which) -> {
                    String weight = editTextWeight.getText().toString().trim();
                    if (!weight.isEmpty()) {
                        viewModel.updatePickup(pickupId, Double.parseDouble(weight));
                    } else {
                        Toast.makeText(this, "Weight is required to complete the pickup.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at the fetched coordinates and move the camera
        LatLng pickupLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 15)); // Zoom level 15
    }
}
