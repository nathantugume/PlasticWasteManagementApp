package com.ugwebstudio.plasticwastemanagementapp.ui.collector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.ui.MapsActivity;

import java.util.HashMap;
import java.util.Map;

public class GetStartedCollectorActivity extends AppCompatActivity {
    // Firebase Firestore instance
    private FirebaseFirestore db;

    // Firebase Storage instance
    private FirebaseStorage storage;
    private StorageReference storageRef;

    // Declare views
    private TextInputEditText businessNameEditText;
    private TextInputEditText businessDescriptionEditText;
    private TextInputEditText servicesOfferedEditText;
    private ImageView businessLogoImageView;

    // Request code for image selection
    private static final int PICK_IMAGE_REQUEST = 1;

    // Selected image URI
    private Uri selectedImageUri;

    // Selected location
    private String selectedLocation;
    private SharedPreferences sharedPreferences;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize views
        businessNameEditText = findViewById(R.id.business_name_edit_text);
        businessDescriptionEditText = findViewById(R.id.business_description_edit_text);
        servicesOfferedEditText = findViewById(R.id.services_offered_edit_text);
        businessLogoImageView = findViewById(R.id.business_logo_image_view);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        UID = sharedPreferences.getString("UID",null);

        boolean isFirstLogin = sharedPreferences.getBoolean("isFirstLogin",true);

        // Set click listener for the select logo button
        Button selectLogoButton = findViewById(R.id.select_logo_button);
        selectLogoButton.setOnClickListener(view -> selectLogo());

        // Set click listener for the register business button
        Button registerButton = findViewById(R.id.register_business_button);
        registerButton.setOnClickListener(view -> registerBusiness());

        // Set click listener for the select location button
        Button selectLocationButton = findViewById(R.id.location_button);
        selectLocationButton.setOnClickListener(view -> selectLocation());
    }

    private void selectLocation() {
        // Launch map activity to select location
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, MapsActivity.REQUEST_CODE_MAP);
    }

    private void selectLogo() {
        // Create an intent to pick an image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MapsActivity.REQUEST_CODE_MAP && resultCode == RESULT_OK && data != null) {
            // Retrieve selected location from map activity result
            double latitude = data.getDoubleExtra(MapsActivity.EXTRA_LATITUDE, 0);
            double longitude = data.getDoubleExtra(MapsActivity.EXTRA_LONGITUDE, 0);
            String latitudeString = String.valueOf(latitude);
            String longitudeString = String.valueOf(longitude);
            selectedLocation = latitudeString+","+longitudeString;
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            selectedImageUri = data.getData();

            // Set the selected image to the ImageView
            businessLogoImageView.setImageURI(selectedImageUri);
        }
    }


    private void registerBusiness() {
        // Validate user inputs
        String businessName = businessNameEditText.getText().toString().trim();
        String businessDescription = businessDescriptionEditText.getText().toString().trim();
        String servicesOffered = servicesOfferedEditText.getText().toString().trim();

        if (TextUtils.isEmpty(businessName) || TextUtils.isEmpty(businessDescription) || TextUtils.isEmpty(servicesOffered)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if a logo is selected
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select a logo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload the logo to Firebase Storage
        uploadLogo(selectedImageUri, businessName, businessDescription, servicesOffered);
    }

    private void uploadLogo(Uri imageUri, String businessName, String businessDescription, String servicesOffered) {
        // Create a reference to store the image
        StorageReference logoRef = storageRef.child("logos/" + businessName + ".jpg");

        // Upload the image to Firebase Storage
        logoRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    logoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save business information with the image URL to Firestore
                        saveBusinessData(businessName, businessDescription, servicesOffered, uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(GetStartedCollectorActivity.this, "Failed to upload logo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveBusinessData(String businessName, String businessDescription, String servicesOffered, String logoUrl) {
        // Prepare data to save in Firestore
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("businessName", businessName);
        businessData.put("businessDescription", businessDescription);
        businessData.put("servicesOffered", servicesOffered);
        businessData.put("logoUrl", logoUrl);
        businessData.put("location",selectedLocation);
        businessData.put("uid",UID);

        // Upload business information to Firestore
        db.collection("collectors")
                .document()
                .set(businessData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(GetStartedCollectorActivity.this, "Business registered successfully", Toast.LENGTH_SHORT).show();

                    // Update isFirstLogin field to false for the current user
                     FirebaseFirestore.getInstance().collection("users")
                                .document(UID)
                                .update("isFirstLogin", false)
                                .addOnSuccessListener(aVoid1 -> {
                                    // isFirstLogin field updated successfully
                                    // Finish the activity after successful registration
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // Error updating isFirstLogin field
                                    Toast.makeText(GetStartedCollectorActivity.this, "Failed to update isFirstLogin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });


                    finish(); // Finish the activity after successful registration
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(GetStartedCollectorActivity.this, "Failed to register business: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
