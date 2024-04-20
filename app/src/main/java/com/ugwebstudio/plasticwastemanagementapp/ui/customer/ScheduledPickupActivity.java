package com.ugwebstudio.plasticwastemanagementapp.ui.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ugwebstudio.plasticwastemanagementapp.R;

public class ScheduledPickupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_pickup);


        // Assuming you have initialized Firebase Firestore and FirebaseAuth
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Get the current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String currentUserName = sharedPreferences.getString("UID", ""); // Assuming "UID" is the key





        TextView textPickupSelectedBy = findViewById(R.id.text_pickup_scheduledby);
        TextView textPickupInfo = findViewById(R.id.text_pickup_info);
        TextView textPickupType = findViewById(R.id.text_pickup_type);
        TextView textPickupStatus = findViewById(R.id.text_pickup_status);
        TextView textPickupCollector = findViewById(R.id.text_pickup_collector);
        MaterialCardView scheduleInfoCard = findViewById(R.id.schedule_info_card);
//        TextView noScheduleText = findViewById(R.id.noScheduleText);

// Get the current user ID
        String currentUserId = auth.getCurrentUser().getUid();

// Assuming you have a collection named "pickups" in Firestore
// and a document for each pickup with fields: selectedBy, date, time, types, status, collector
        db.collection("pickups")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String status = documentSnapshot.getString("status");

                            if (!status.equals("collected")) {
                                String selectedBy = currentUserName; // Assuming you have currentUserName
                                String date = documentSnapshot.getString("date");
                                String time = documentSnapshot.getString("time");
                                String types = documentSnapshot.getString("selectedChipTypes");
                                String collector = documentSnapshot.getString("collector");

                                // Set the values to the TextViews
                                textPickupSelectedBy.setText(selectedBy);
                                textPickupInfo.setText("Date: " + date + "\nTime: " + time);
                                textPickupType.setText(types);
                                textPickupStatus.setText(status);
                                textPickupCollector.setText(collector);
                            } else {
                                Toast.makeText(ScheduledPickupActivity.this, "Already collected!! no new schedule", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        // No documents found
                        Toast.makeText(ScheduledPickupActivity.this, "No Schedule made Yet", Toast.LENGTH_SHORT).show();
                        scheduleInfoCard.setVisibility(View.GONE);
                        //noScheduleText.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Toast.makeText(ScheduledPickupActivity.this, "Sorry we are unable to fetch your results at the moment!!", Toast.LENGTH_LONG).show();
                    }
                });


    }
}