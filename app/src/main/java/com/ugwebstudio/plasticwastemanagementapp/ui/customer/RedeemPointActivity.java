package com.ugwebstudio.plasticwastemanagementapp.ui.customer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ugwebstudio.plasticwastemanagementapp.R;

public class RedeemPointActivity extends AppCompatActivity {

    private TextView textTitle,redeemMessage;
    private RadioGroup redeemOptions;
    private Button btnRedeem;

    private int userPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_point);

        // Initialize views
        textTitle = findViewById(R.id.text_title);
        redeemOptions = findViewById(R.id.redeemOptions);
        btnRedeem = findViewById(R.id.btn_redeem);

        redeemMessage = findViewById(R.id.redeem_message);

        // Fetch user points from Firestore
        fetchUserPoints();

        // Check if the user's current pickup is complete to enable/disable redemption options
        checkPickupCompletion();

        // Calculate and set redemption options based on user points
        calculateRedemptionOptions();

        // Handle redeem button click
        btnRedeem.setOnClickListener(view -> redeemPoints());
    }

    // Fetch user points from Firestore
    @SuppressLint("SetTextI18n")
    private void fetchUserPoints() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("points")
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userPoints = documentSnapshot.getLong("points").intValue();
                        textTitle.setText("Redeem Your Points : (" + userPoints + ")");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch user points", Toast.LENGTH_SHORT).show());
    }

    // Check if the user's current pickup is complete to enable/disable redemption options
// Check if the user's current pickup is complete to enable/disable redemption options
    private void checkPickupCompletion() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("pickups")
                .whereEqualTo("userId", userID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean isPickupComplete = false;

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String status = documentSnapshot.getString("status");

                        // Assuming pickup status "completed" indicates a completed pickup
                        if (status != null && status.equals("completed")) {
                            isPickupComplete = true;
                            break;
                        }
                    }

                    if (isPickupComplete) {
                        // Enable redemption options
                        for (int i = 0; i < redeemOptions.getChildCount(); i++) {
                            redeemOptions.getChildAt(i).setEnabled(true);
                        }
                    } else {
                        // Disable redemption options
                        redeemMessage.setVisibility(View.VISIBLE);
                        btnRedeem.setEnabled(false);
                        for (int i = 0; i < redeemOptions.getChildCount(); i++) {
                            redeemOptions.getChildAt(i).setEnabled(false);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch pickups
                    Toast.makeText(this, "Failed to fetch pickups: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // Calculate and set redemption options based on user points
    @SuppressLint("SetTextI18n")
    private void calculateRedemptionOptions() {
        int cashPoints = 500;
        int airtimePoints = 100;
        int dataPoints = 100;

        RadioButton radioCash = findViewById(R.id.radioCash);
        RadioButton radioAirtime = findViewById(R.id.radioAirtime);
        RadioButton radioData = findViewById(R.id.radioData);

        // Fetch user's points from Firebase
        fetchUserPoints(points -> {
            // Calculate cash, airtime, and data the user can redeem
            int cashAmount = points / cashPoints * 1000; // 1000 UGX per 500 points
            int airtimeAmount = points / airtimePoints * 1000; // 10 airtime per 100 points
            int dataAmount = points / dataPoints * 100; // 100 MB data per 100 points

            // Set redemption options text
            radioCash.setText("Redeem Cash (" + cashAmount + " UGX)");
            radioAirtime.setText("Redeem Airtime (" + airtimeAmount + " airtime)");
            radioData.setText("Redeem Data (" + dataAmount + " MB data)");
        });
    }

    // Fetch user points from Firestore
    @SuppressLint("SetTextI18n")
    private void fetchUserPoints(OnSuccessListener<Integer> successListener) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("points")
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int userPoints = documentSnapshot.getLong("points").intValue();
                        textTitle.setText("Redeem Your Points : (" + userPoints + ")");
                        successListener.onSuccess(userPoints); // Pass user points to the success listener
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch user points", Toast.LENGTH_SHORT).show());
    }


    // Handle redeem button click
    private void redeemPoints() {
        int selectedId = redeemOptions.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton radioButton = findViewById(selectedId);
            String option = radioButton.getText().toString();

            // Implement logic to redeem points based on the selected option
            Toast.makeText(this, "Redeem option: " + option, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please select a redemption option", Toast.LENGTH_SHORT).show();
        }
    }
}
