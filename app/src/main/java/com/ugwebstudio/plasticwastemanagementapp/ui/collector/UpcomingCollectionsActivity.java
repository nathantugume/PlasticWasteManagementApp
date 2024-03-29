package com.ugwebstudio.plasticwastemanagementapp.ui.collector;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.adapters.PickupAdapter;
import com.ugwebstudio.plasticwastemanagementapp.classes.Pickup;

import java.util.ArrayList;
import java.util.List;

public class UpcomingCollectionsActivity extends AppCompatActivity {

    private RecyclerView collectionsRecyclerView;
    private PickupAdapter pickupAdapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference pickupsRef; // Declare pickupsRef variable
    private static final String[] STATUS_OPTIONS = {"Scheduled", "Completed", "Pending", "Cancelled"};
    private static final String TAG = "UPCOMING STATUS";

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_collections);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Pickups ...");
        progressDialog.setCancelable(false);

        pickupsRef = db.collection("pickups");

        collectionsRecyclerView = findViewById(R.id.collectionsRecyclerView);
        pickupAdapter = new PickupAdapter();

        collectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        collectionsRecyclerView.setAdapter(pickupAdapter);

        // Fetch pickups from Firestore and set them in RecyclerView


        Spinner statusSpinner = findViewById(R.id.statusSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, STATUS_OPTIONS);

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        statusSpinner.setAdapter(adapter);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedStatus = adapterView.getItemAtPosition(position).toString();
                fetchPickupsByStatus(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
        fetchPickups();
    }

    private void fetchPickupsByStatus(String status) {
        progressDialog.show();
        Query query = pickupsRef.whereEqualTo("status", status);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Pickup> pickups = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Pickup pickup = document.toObject(Pickup.class);
                    pickup.setDocumentId(document.getId());
                    pickups.add(pickup);
                }
                // Update the RecyclerView with the filtered pickups
                pickupAdapter.setPickupList(pickups);
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
                // Handle unsuccessful fetch
                Log.e(TAG, "Error getting pickups by status", task.getException());
            }
        });
    }

    private void fetchPickups() {
        // Fetch pickups from Firestore
        // You can use Firestore query to retrieve pickups and then set them in RecyclerView adapter

        progressDialog.show();

        Query query = pickupsRef.whereEqualTo("status", "Scheduled");
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Pickup> pickups = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Pickup pickup = document.toObject(Pickup.class);
                    pickup.setDocumentId(document.getId());
                    pickups.add(pickup);
                }
                // Update the RecyclerView with the filtered pickups
                pickupAdapter.setPickupList(pickups);
                progressDialog.dismiss();
            } else {
                // Handle unsuccessful fetch
                progressDialog.dismiss();
                Log.e(TAG, "Error getting pickups by status", task.getException());
            }
        });


        List<Pickup> pickups = new ArrayList<>(); // Replace this with actual pickups fetched from Firestore
        pickupAdapter.setPickupList(pickups);
        progressDialog.dismiss();
    }
}