package com.ugwebstudio.plasticwastemanagementapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.ugwebstudio.plasticwastemanagementapp.ui.Donor.DonorDashboardActivity;
import com.ugwebstudio.plasticwastemanagementapp.ui.Donor.GetStartedDonorActivity;
import com.ugwebstudio.plasticwastemanagementapp.ui.Organisation.GetStartedOrganizationActivity;
import com.ugwebstudio.plasticwastemanagementapp.ui.Organisation.OrganisationDashboardActivity;
import com.ugwebstudio.plasticwastemanagementapp.ui.collector.CollectorDashboardActivity;
import com.ugwebstudio.plasticwastemanagementapp.ui.collector.GetStartedCollectorActivity;
import com.ugwebstudio.plasticwastemanagementapp.ui.customer.GetStartedIndividualActivity;
import com.ugwebstudio.plasticwastemanagementapp.ui.customer.UserDashboardActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;

    private SharedPreferences sharedPreferences;

    private String  accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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


        // Set click listener for login button
        loginButton.setOnClickListener(view -> loginUser());

        // Set click listener for sign up text
        signupText.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
    }




    private void loginUser() {
        String email = mEmailLayout.getEditText().getText().toString().trim();
        String password = mPasswordLayout.getEditText().getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        progressDialog.show();

        // Authenticate user using Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // Hide progress dialog
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        // After successful authentication, retrieve user's account type from Firestore
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            db.collection("users").document(Objects.requireNonNull(user.getEmail()))
                                    .get()
                                    .addOnCompleteListener(documentTask -> {
                                        if (documentTask.isSuccessful()) {
                                            DocumentSnapshot document = documentTask.getResult();
                                            if (document != null && document.exists()) {
                                                // Retrieve account type from Firestore
                                                 accountType = document.getString("accountType");
                                                 String UID = document.getId();
                                                 Boolean isFirstLogin = document.getBoolean("isFirstLogin");
                                                // save account type for later user
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("accountType", accountType);
                                                editor.putString("UID",UID);
                                                editor.putBoolean("isFirstLogin", Boolean.TRUE.equals(isFirstLogin));
                                                editor.apply();

                                                // Navigate user to the corresponding dashboard activity
                                                if (accountType != null) {
                                                    startActivityBasedOnAccountType();
                                                    finish(); // Finish LoginActivity to prevent user from coming back here
                                                } else {
                                                    // Account type not found
                                                    Toast.makeText(LoginActivity.this, "Account type not found", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                // Document not found
                                                Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            // Error getting document
                                            Toast.makeText(LoginActivity.this, "Error getting user data: " + documentTask.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // User is null
                            Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Authentication failed
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
                case "Organisation":
                    startActivity(new Intent(LoginActivity.this, OrganisationDashboardActivity.class));
                    break;
                case "Donor":
                    startActivity(new Intent(LoginActivity.this, DonorDashboardActivity.class));
                    break;
                case "Collector":
                    startActivity(new Intent(LoginActivity.this, CollectorDashboardActivity.class));
                    break;
                default:
// Account type not recognized
                    Toast.makeText(LoginActivity.this, "Unknown account type", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }



}
