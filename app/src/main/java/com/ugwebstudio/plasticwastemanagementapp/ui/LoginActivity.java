package com.ugwebstudio.plasticwastemanagementapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.classes.Scheduler;
import com.ugwebstudio.plasticwastemanagementapp.ui.admin.AdminDashboardActivity;
import com.ugwebstudio.plasticwastemanagementapp.ui.collector.CollectorDashboardActivity;
import com.ugwebstudio.plasticwastemanagementapp.ui.customer.UserDashboardActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;

    private SharedPreferences sharedPreferences;

    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Scheduler.schedulePickupCheck(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        mEmailLayout = findViewById(R.id.email);
        mPasswordLayout = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        TextView signupText = findViewById(R.id.signup_txt);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        // Check if user is already logged in
        checkUserLoggedIn();

        // Set click listener for login button
        loginButton.setOnClickListener(view -> loginUser());

        // Set click listener for sign up text
        signupText.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
    }

    private void checkUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, retrieve the stored account type
            String savedAccountType = sharedPreferences.getString("accountType", null);
            if (savedAccountType != null) {
                accountType = savedAccountType;
                startActivityBasedOnAccountType();
                finish();
            } else {
                // If account type is not saved in SharedPreferences, fetch it from Firestore
                fetchAccountType(currentUser.getEmail());
            }
        }
    }

    private void fetchAccountType(String email) {
        db.collection("users").document(email)
                .get()
                .addOnCompleteListener(documentTask -> {
                    if (documentTask.isSuccessful()) {
                        DocumentSnapshot document = documentTask.getResult();
                        if (document != null && document.exists()) {
                            accountType = document.getString("accountType");
                            String UID = document.getString("uid");
                            String phone = document.getString("phone");
                            Boolean isFirstLogin = document.getBoolean("isFirstLogin");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("accountType", accountType);
                            editor.putString("UID", UID);
                            editor.putBoolean("isFirstLogin", Boolean.TRUE.equals(isFirstLogin));
                            editor.putString("phone", phone);
                            editor.putString("email", email);
                            editor.apply();

                            startActivityBasedOnAccountType();
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Error getting user data: " + documentTask.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser() {
        String email = mEmailLayout.getEditText().getText().toString().trim();
        String password = mPasswordLayout.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            fetchAccountType(user.getEmail());
                        } else {
                            Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startActivityBasedOnAccountType() {
        if (accountType != null) {
            switch (accountType) {
                case "Individual":
                    startActivity(new Intent(LoginActivity.this, UserDashboardActivity.class));
                    break;
                case "admin":
                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                    break;
                case "Collector":
                    startActivity(new Intent(LoginActivity.this, CollectorDashboardActivity.class));
                    break;
                default:
                    Toast.makeText(LoginActivity.this, "Unknown account type", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
