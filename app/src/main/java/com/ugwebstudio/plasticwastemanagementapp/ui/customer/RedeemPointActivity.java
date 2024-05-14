package com.ugwebstudio.plasticwastemanagementapp.ui.customer;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.classes.SmsSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedeemPointActivity extends AppCompatActivity {

    private TextView textTitle, redeemMessage;
    private RadioGroup redeemOptions;
    private Button btnRedeem;

    private int userPoints;
    private String phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_point);

        // Initialize views
        textTitle = findViewById(R.id.text_title);
        redeemOptions = findViewById(R.id.redeemOptions);
        btnRedeem = findViewById(R.id.btn_redeem);

        redeemMessage = findViewById(R.id.redeem_message);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", ""); // Assuming "UID" is the key

        // Fetch user points from Firestore
        fetchUserPoints();

        // Check if the user's current pickup is complete to enable/disable redemption options
        checkPickupCompletion();

        // Calculate and set redemption options based on user points
        calculateRedemptionOptions();

        // Handle redeem button click
        btnRedeem.setOnClickListener(view -> redeemPoints());

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
                        if (status != null && status.equals("Completed")) {
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


            String amount = extractAmount(option); // Implement method to extract amount based on option

            new Thread(() -> {
                try {
                    sendPostRequest(phone, amount, String.valueOf(userPoints));
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Error sending request: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        } else {
            Toast.makeText(this, "Please select a redemption option", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPostRequest(String phone, String amount, String pointsToDeduct) throws IOException {
        Log.d("sendPostRequest", "Starting POST request");
        URL url = new URL("https://www.easypay.co.ug/api/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInputString = createJsonPayload(phone, amount);
        Log.d("sendPostRequest", "Sending JSON: " + jsonInputString);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        Log.d("sendPostRequest", "Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.d("sendPostRequest", "Response: " + response.toString());

                // Parse the JSON response to check for success
                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.getInt("success") == 1) {
                    updatePoints(pointsToDeduct); // Update the points in Firestore only if success
                    runOnUiThread(() -> Toast.makeText(this, "Airtime/Data redeemed successfully", Toast.LENGTH_LONG).show());
                } else {
                    // Handle the case where the API returned success:0
                    String errorMessage = jsonResponse.getString("errormsg");
                    Log.d("sendPostRequest", "Failed to redeem: " + errorMessage);
                    runOnUiThread(() -> Toast.makeText(this, "Failed to redeem points: " + errorMessage, Toast.LENGTH_LONG).show());
                }
            } catch (JSONException e) {
                Log.e("sendPostRequest", "JSON parsing error: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(this, "Parsing error in response", Toast.LENGTH_LONG).show());
            }

        } else {
            Log.d("sendPostRequest", "Failed to redeem points, response code: " + responseCode);
            runOnUiThread(() -> Toast.makeText(this, "Failed to redeem points", Toast.LENGTH_LONG).show());
        }
    }


    private void updatePoints(String pointsToDeduct) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference pointsRef = db.collection("points").document(userID);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot snapshot = transaction.get(pointsRef);
                    long currentPoints = snapshot.getLong("points");
                    long newPoints = currentPoints - Long.parseLong(pointsToDeduct);
                    transaction.update(pointsRef, "points", newPoints);
                    return null;
                }).addOnSuccessListener(unused -> {
                    Log.d("UpdatePoints", "Points deducted successfully");
            SmsSender.sendSms( phone, "You have successfully Redeemed "+pointsToDeduct+" points from Plastic Waste Collection");

        }).addOnFailureListener(e -> Log.e("UpdatePoints", "Failed to deduct points", e));
    }


    private String createJsonPayload(String phone, String amount) {
        String uniqueReferenceId = UUID.randomUUID().toString(); // Generates a random UUID
        return String.format("{\n" +
                "  \"username\": \"\",\n" +
                "  \"password\": \"\",\n" +
                "  \"action\": \"paybill\",\n" +
                "  \"provider\": \"mtn\",\n" +
                "  \"phone\": \"%s\",\n" +
                "  \"amount\": \"%s\",\n" +
                "  \"reference\": \"%s\"\n" +
                "}", phone, amount, uniqueReferenceId);
    }
    private String extractAmount(String option) {
        Pattern pattern = Pattern.compile("\\d+"); // Regex to find numbers in the string
        Matcher matcher = pattern.matcher(option);
        if (matcher.find()) {
            return matcher.group(); // Returns the first number found in the option string
        }
        return "0"; // Default return value in case no numbers are found
    }



}