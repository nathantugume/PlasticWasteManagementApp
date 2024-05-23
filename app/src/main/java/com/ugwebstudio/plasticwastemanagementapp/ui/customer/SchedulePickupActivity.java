package com.ugwebstudio.plasticwastemanagementapp.ui.customer;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.ui.MapsActivity;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SchedulePickupActivity extends AppCompatActivity {

    private TextInputEditText pickupDateEditText, pickupLocationEditText;
    private TextInputEditText pickupTimeEditText;
    private SwitchMaterial notificationSwitch;
    private ChipGroup chipGroup;
    private Calendar calendar;

    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private ProgressDialog progressDialog;

    private static final int REQUEST_CODE_MAP = 101;

    // Handle the result from MapsActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAP) {
            if (resultCode == RESULT_OK && data != null) {
                // Retrieve selected location from MapsActivity
                double latitude = data.getDoubleExtra(MapsActivity.EXTRA_LATITUDE, 0);
                double longitude = data.getDoubleExtra(MapsActivity.EXTRA_LONGITUDE, 0);

                // Update the TextInputEditText with the selected location
                TextInputLayout pickupLocationLayout = findViewById(R.id.pickupLocationLayout);
                pickupLocationLayout.getEditText().setText(String.format("%f, %f", latitude, longitude));
            } else {
                Toast.makeText(this, "No location selected", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_pickup);

        pickupDateEditText = findViewById(R.id.pickupDate);
        pickupTimeEditText = findViewById(R.id.pickupTime);
        pickupLocationEditText = findViewById(R.id.pickupLocation);

        notificationSwitch = findViewById(R.id.notificationSwitch);
        MaterialButton schedulePickupButton = findViewById(R.id.scheduleButton);
        chipGroup = findViewById(R.id.chipGroup);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        calendar = Calendar.getInstance();

        pickupDateEditText.setOnClickListener(v -> showDatePicker());

        pickupTimeEditText.setOnClickListener(v -> showTimePicker());

        schedulePickupButton.setOnClickListener(view -> saveDataToFirebase());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Scheduling Pickup...");
        progressDialog.setCancelable(false);

        // Find the TextInputLayout and set up click listener for the end icon
        TextInputLayout pickupLocationLayout = findViewById(R.id.pickupLocationLayout);
        pickupLocationLayout.setEndIconOnClickListener(v -> {
            // Start MapsActivity to select location
            Intent intent = new Intent(SchedulePickupActivity.this, MapsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_MAP);
        });

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


    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateLabel();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    updateTimeLabel();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void updateDateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        pickupDateEditText.setText(sdf.format(calendar.getTime()));
    }

    private void updateTimeLabel() {
        String myFormat = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        pickupTimeEditText.setText(sdf.format(calendar.getTime()));
    }

    private boolean validateInputs() {
        // Perform your validation here
        // For example, check if the date and time are selected
        if (pickupDateEditText.getText().toString().isEmpty() || pickupTimeEditText.getText().toString().isEmpty() || pickupLocationEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveDataToFirebase() {
        // Prepare data and save to Firebase database
        if (validateInputs()) {
            // Your code to save data to Firebase
            String selectedDate = pickupDateEditText.getText().toString();
            String selectedTime = pickupTimeEditText.getText().toString();
            String pickupLocation = pickupLocationEditText.getText().toString();
            boolean notificationEnabled = notificationSwitch.isChecked();

            // Get selected chips
            StringBuilder selectedChips = new StringBuilder();
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    selectedChips.append(chip.getText()).append(", ");
                }
            }

            // Remove trailing comma and space
            String selectedChipTypes = selectedChips.toString().trim();
            if (selectedChipTypes.endsWith(", ")) {
                selectedChipTypes = selectedChipTypes.substring(0, selectedChipTypes.length() - 2);
            }

            progressDialog.show();

            // Get the current date
            Date currentDate = new Date();

// Format the date as needed
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(currentDate);

            // Save data to Firestore
            Map<String, Object> pickupData = new HashMap<>();
            pickupData.put("userId", currentUser.getUid());
            pickupData.put("date", selectedDate);
            pickupData.put("time", selectedTime);
            pickupData.put("notificationEnabled", notificationEnabled);
            pickupData.put("selectedChipTypes", selectedChipTypes);
            pickupData.put("status", "Scheduled");
            pickupData.put("collector", "not selected");
            pickupData.put("scheduledDate", formattedDate);
            pickupData.put("pickupLocation", pickupLocation);

            db.collection("pickups").document()
                    .set(pickupData)
                    .addOnSuccessListener(aVoid -> {
                        // Pickup data saved successfully
                        int points = 100;
                        String userId = currentUser.getUid();
                      //  String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                        db.collection("points").document(userId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // Document exists, update the points
                                        int existingPoints = documentSnapshot.getLong("points").intValue();
                                        int updatedPoints = existingPoints + points;

                                        Map<String, Object> pointsData = new HashMap<>();
                                        pointsData.put("user", userId); // Use UID or another unique identifier
                                        pointsData.put("date", formattedDate);
                                        pointsData.put("points", updatedPoints);

                                        db.collection("points").document(userId)
                                                .set(pointsData)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    // Points data updated successfully
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SchedulePickupActivity.this, "Congratulations!! You have received 100 points for scheduling!!", Toast.LENGTH_SHORT).show();
                                                    finish(); // Close the activity after successful scheduling
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Failed to update points data
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SchedulePickupActivity.this, "Failed to update points. Please try again later.", Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        // Document does not exist, create new document
                                        Map<String, Object> pointsData = new HashMap<>();
                                        pointsData.put("user", userId); // Use UID or another unique identifier
                                        pointsData.put("date", formattedDate);
                                        pointsData.put("points", points);

                                        db.collection("points").document(userId)
                                                .set(pointsData)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    // Points data saved successfully
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SchedulePickupActivity.this, "Congratulations!! You have received 100 points for scheduling!!", Toast.LENGTH_SHORT).show();
                                                    finish(); // Close the activity after successful scheduling
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Failed to save points data
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SchedulePickupActivity.this, "Failed to add points. Please try again later.", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Failed to retrieve points document
                                    progressDialog.dismiss();
                                    Toast.makeText(SchedulePickupActivity.this, "Failed to retrieve points information. Please try again later.", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Failed to save pickup data
                        progressDialog.dismiss();
                        Toast.makeText(SchedulePickupActivity.this, "Failed to schedule pickup. Please try again.", Toast.LENGTH_SHORT).show();
                    });



        }
    }
}


