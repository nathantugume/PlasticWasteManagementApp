package com.ugwebstudio.plasticwastemanagementapp.ui.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.adapters.PickupsAdapter;
import com.ugwebstudio.plasticwastemanagementapp.classes.Pickup;

import java.util.ArrayList;
import java.util.List;

public class CustomerReportActivity extends AppCompatActivity {
    private RecyclerView pickupsRecyclerView;
    private PickupsAdapter pickupsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_report);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.customer_profile){
                startActivity(new Intent(this, CustomerProfileActivity.class));
                return true;
            }
            return false;
        });

        topAppBar.setNavigationOnClickListener(view -> onBackPressed());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_nav_home){
                startActivity(new Intent(this, UserDashboardActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.bottom_nav_recycling){
                startActivity(new Intent(this, RecyclingActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.bottom_nav_redeem){
                startActivity(new Intent(this, RedeemPointActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.bottom_nav_reports){
                startActivity(new Intent(this, CustomerReportActivity.class));
                return true;
            }

            return false;
        });

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        pickupsRecyclerView = findViewById(R.id.pickupsRecyclerView);
        pickupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Assume you have a method to fetch data
        List<Pickup> pickups = fetchData();
        pickupsAdapter = new PickupsAdapter(pickups);
        pickupsRecyclerView.setAdapter(pickupsAdapter);
    }

    private List<Pickup> fetchData() {
        List<Pickup> pickupsList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();  // Get instance of your Firestore database

        db.collection("pickups")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Pickup pickup = document.toObject(Pickup.class);
                            pickupsList.add(pickup);
                        }
                        updateRecyclerView(pickupsList);
                        pickupsAdapter.setPickups(pickupsList);
                        Log.d("DataFetch", "Data fetched.");

                    } else {
                        Log.w("DataFetch", "Error getting documents.", task.getException());
                    }
                });
        return pickupsList;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateRecyclerView(List<Pickup> pickups) {
        Log.d("UpdateView", "Updating RecyclerView with new data");

        if (pickupsRecyclerView.getAdapter() == null) {
            Log.d("UpdateView", "Adapter not initialized, setting new adapter");
            pickupsRecyclerView.setAdapter(new PickupsAdapter(pickups));
        } else {
            Log.d("UpdateView", "Adapter already initialized, updating data");
            PickupsAdapter adapter = (PickupsAdapter) pickupsRecyclerView.getAdapter();
            adapter.setPickups(pickups);
            adapter.notifyDataSetChanged();
            Log.d("UpdateView", "Data set changed, RecyclerView updated");
        }
    }



}