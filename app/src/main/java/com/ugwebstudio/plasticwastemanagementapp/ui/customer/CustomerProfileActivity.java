package com.ugwebstudio.plasticwastemanagementapp.ui.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.classes.SmsSender;

public class CustomerProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("email", ""); // Default to empty if not found
        Log.d("userId",userId);
        setupToolbar();
        fetchUserData();


         Button editButton = findViewById(R.id.editPassword);
         editButton.setOnClickListener(view -> updateProfileAndPassword());
    }

    private void setupToolbar() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.customer_profile) {
                startActivity(new Intent(this, CustomerProfileActivity.class));
                return true;
            }
            return false;
        });
        topAppBar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void fetchUserData() {

        if (!userId.isEmpty()) {
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Assuming these fields exist in your Firestore
                            String name = documentSnapshot.getString("fullName");
                            String email = documentSnapshot.getString("email");
                            String phone = documentSnapshot.getString("phone");
                            updateProfileUI(name, email, phone);
                        } else {
                            Log.e("Firebase", "No such user!");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firebase", "Error getting user details", e));
        }
    }

    private void updateProfileUI(String name, String email, String phone) {
        TextInputEditText userNameTextView = findViewById(R.id.userNameTextView);
        TextInputEditText userEmailTextView = findViewById(R.id.userEmailTextView);
        TextInputEditText phoneTextView = findViewById(R.id.phoneTextView);

        userNameTextView.setText(name);
        userEmailTextView.setText(email);
        phoneTextView.setText(phone);
    }

    public void updateProfileAndPassword() {
        TextInputEditText userNameTextView = findViewById(R.id.userNameTextView);
        TextInputEditText userEmailTextView = findViewById(R.id.userEmailTextView);
        TextInputEditText phoneTextView = findViewById(R.id.phoneTextView);
        TextInputEditText passwordTextView = findViewById(R.id.passwordTextView);
        TextInputEditText currentTextView = findViewById(R.id.currentPasswordTextView);

        String name = userNameTextView.getText().toString();
        String email = userEmailTextView.getText().toString();
        String phone = phoneTextView.getText().toString();
        String newPassword = passwordTextView.getText().toString();
        String currentPassword = currentTextView.getText().toString();



        // Update Firestore document for profile information
        db.collection("users").document(userId)

                .update("name", name, "email", email, "phone", phone)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UpdateProfile", "User profile updated successfully");
                    SmsSender.sendSms(phone,"Dear " +name+" your account has been updated successfully");
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e("UpdateProfile", "Error updating user profile", e));

        // Check if password field is not empty and then update password
        if (!newPassword.trim().isEmpty()) {
            if (currentUser != null) {
                currentUser.reauthenticate(EmailAuthProvider.getCredential(email, currentPassword)) // Assumes currentPassword is available
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                currentUser.updatePassword(newPassword)
                                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Log.e("UpdatePassword", "Error updating password", e));
                            } else {
                                Log.e("ReAuth", "Re-authentication failed.", task.getException());
                                // Handle re-authentication failure
                            }
                        });
            }
        }
    }



}
