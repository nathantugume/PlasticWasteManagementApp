package com.ugwebstudio.plasticwastemanagementapp.ui.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.ui.LoginActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import firebase.com.protolitewrapper.BuildConfig;

public class UserDashboardActivity extends AppCompatActivity {

    private MaterialCardView schedulePickUpCard, plasticSortingCard, recyclingCard, collectionCard;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private ProgressBar progressBar;

    private ProgressDialog progressDialog;

    private MaterialToolbar topAppBar;
    private TextView textPercentage;
    private TextView currentPointsText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        textPercentage = findViewById(R.id.textPercentage);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("fetching Points...");
        fetchPoints();

        topAppBar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        topAppBar.setNavigationOnClickListener(view -> drawerLayout.open());
        currentPointsText = findViewById(R.id.currentPoints);

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.scheduled_pickup){
                drawerLayout.close();
                startActivity(new Intent(UserDashboardActivity.this, ScheduledPickupActivity.class));

            }

           if(item.getItemId() == R.id.redeem_points){
               drawerLayout.close();
                startActivity(new Intent(UserDashboardActivity.this, RedeemPointActivity.class));
           }

           if (item.getItemId() == R.id.sign_out){
               drawerLayout.close();
               mAuth.getCurrentUser();
               mAuth.signOut();
               startActivity(new Intent(UserDashboardActivity.this, LoginActivity.class));
               finish();

           }
           
           if (item.getItemId() == R.id.share){
               drawerLayout.close();
               share();
           }
            return false;
        });

        progressBar = findViewById(R.id.progressBar);






        //schedule pickup
        schedulePickUpCard = findViewById(R.id.schedule_pickup_card);
        schedulePickUpCard.setOnClickListener(view -> startActivity(new Intent(UserDashboardActivity.this, SchedulePickupActivity.class)));

        //plastic sorting
        plasticSortingCard = findViewById(R.id.plastic_sorting_card);
        plasticSortingCard.setOnClickListener(view -> startActivity(new Intent(UserDashboardActivity.this, PlasticSortingActivity.class)));

        //recycling card
        recyclingCard = findViewById(R.id.recycling_card);
        recyclingCard.setOnClickListener(view -> startActivity(new Intent(UserDashboardActivity.this, RecyclingActivity.class)));

        //collection center
        collectionCard = findViewById(R.id.collection_center_card);
        collectionCard.setOnClickListener(view -> startActivity(new Intent(UserDashboardActivity.this, CollectionCentersActivity.class)));
    }

    private void share() {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        intent.setType("*/*");

        // Append file and send Intent
        File originalApk = new File(filePath);

        try {
            //Make new directory in new location=
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk");
            //If directory doesn't exists create new
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            //Get application's name and convert to lowercase
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ","").toLowerCase() + ".apk");
            //If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            //Copy file to new location
            InputStream in = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                in = Files.newInputStream(originalApk.toPath());
            }
            OutputStream out = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                out = Files.newOutputStream(tempFile.toPath());
            }

            byte[] buf = new byte[1024];
            int len;
            while (true) {
                assert in != null;
                if (!((len = in.read(buf)) > 0)) break;
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            //Open share dialog
//          intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
            Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", tempFile);
//          intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            startActivity(Intent.createChooser(intent, "Share app via"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("SetTextI18n")
    private void fetchPoints() {
        // Get the current user's ID
        progressDialog.show();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the Firestore collection where points are stored
        CollectionReference pointsRef = FirebaseFirestore.getInstance().collection("points");

        // Query to retrieve the points for the current user
        pointsRef.document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Retrieve the points value from the document
                    Long pointsValue = document.getLong("points");

                    // Store the points value in the 'points' variable
                    int points = pointsValue != null ? pointsValue.intValue() : 0;

                    topAppBar.setSubtitle(String.valueOf(points));

                    progressDialog.dismiss();

                    // Calculate percentage and update progress bar
                    int maxPoints = 1000; // Set the maximum points value
                    int percentage = maxPoints != 0 ? (points * 100) / maxPoints : 0; // Avoid division by zero
                    progressBar.setProgress(points);

                    // Update text view with percentage
                    textPercentage.setText(percentage + "%");
                    currentPointsText.setText(points+"/");

                    // Now you can use the 'points' variable as needed
                    Log.d("Percentage", "Current percent: " + percentage);
                } else {
                    progressDialog.dismiss();
                    // Document doesn't exist, handle appropriately
                    Toast.makeText(this, "Sorry you don't have any points at the moment", Toast.LENGTH_SHORT).show();

                    Log.d("Points", "Document does not exist");
                }
            } else {
                progressDialog.dismiss();
                // Failed to retrieve points, handle appropriately
                Toast.makeText(this, "Failed to fetch points try again later !!", Toast.LENGTH_SHORT).show();
                Log.d("Points", "Failed to retrieve points: ", task.getException());
            }
        });
    }

}