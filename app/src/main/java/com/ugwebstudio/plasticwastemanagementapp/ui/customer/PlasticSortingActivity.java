package com.ugwebstudio.plasticwastemanagementapp.ui.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ugwebstudio.plasticwastemanagementapp.R;

public class PlasticSortingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plastic_sorting);


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
}