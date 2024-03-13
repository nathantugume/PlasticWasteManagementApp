package com.ugwebstudio.plasticwastemanagementapp.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.classes.FirestoreDBManager;
import com.ugwebstudio.plasticwastemanagementapp.classes.User;

import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirestoreDBManager dbManager;

    private TextInputLayout mFullNameLayout;
    private TextInputLayout mEmailLayout;
    private TextInputLayout mPhoneLayout;
    private TextInputLayout mPasswordLayout;
    private TextInputLayout mConfirmPasswordLayout;
    private ChipGroup chipGroup;
    private Chip individualChip, orgChip, donorChip, collectorChip;
     private String accountType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_upactivity);

        mAuth = FirebaseAuth.getInstance();
        dbManager = new FirestoreDBManager();

        // Initialize views
        mFullNameLayout = findViewById(R.id.fname);
        mEmailLayout = findViewById(R.id.email);
        mPhoneLayout = findViewById(R.id.phone);
        mPasswordLayout = findViewById(R.id.password);
        mConfirmPasswordLayout = findViewById(R.id.pickupDate);

        chipGroup = findViewById(R.id.chip_group);

        individualChip = findViewById(R.id.individual_chip);
        orgChip = findViewById(R.id.org_chip);
        donorChip = findViewById(R.id.donor_chip);
        collectorChip = findViewById(R.id.collector_chip);

        Button signUpButton = findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(v -> signUp());

        individualChip.setOnClickListener(view -> {
            handleChipSelection(individualChip);
        });

        orgChip.setOnClickListener(view -> {
            handleChipSelection(orgChip);
        });

        donorChip.setOnClickListener(view -> {
            handleChipSelection(donorChip);
        });

        collectorChip.setOnClickListener(view -> {
            handleChipSelection(collectorChip);
        });

    }

    private void handleChipSelection(Chip chip) {
        // Reset all chips to unchecked state
        Chip individualChip = findViewById(R.id.individual_chip);
        Chip orgChip = findViewById(R.id.org_chip);
        Chip donorChip = findViewById(R.id.donor_chip);
        Chip collectorChip = findViewById(R.id.collector_chip);

        individualChip.setChecked(false);
        orgChip.setChecked(false);
        donorChip.setChecked(false);
        collectorChip.setChecked(false);

        // Set the clicked chip as checked
        chip.setChecked(true);

        // Update the account type based on the clicked chip
        accountType = chip.getText().toString();
    }

    private void signUp() {
        String fullName = mFullNameLayout.getEditText().getText().toString().trim();
        String email = mEmailLayout.getEditText().getText().toString().trim();
        String phone = mPhoneLayout.getEditText().getText().toString().trim();
        String password = mPasswordLayout.getEditText().getText().toString().trim();
        String confirmPassword = mConfirmPasswordLayout.getEditText().getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }



        // Create user account with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save user data to Firestore collection
                        User user = new User(fullName, email, phone, accountType, true);
                        dbManager.addUser(user, success -> {
                            if (success) {
                                Toast.makeText(SignUpActivity.this, "User account created successfully", Toast.LENGTH_SHORT).show();
                                // Navigate to the next activity or perform any other action
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(SignUpActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to create account: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
