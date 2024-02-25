package com.ugwebstudio.plasticwastemanagementapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;
import com.ugwebstudio.plasticwastemanagementapp.R;

public class UserDashboardActivity extends AppCompatActivity {

    private MaterialCardView schedulePickUpCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        schedulePickUpCard = findViewById(R.id.schedule_pickup_card);



        schedulePickUpCard.setOnClickListener(view -> startActivity(new Intent(UserDashboardActivity.this, SchedulePickupActivity.class)));


    }
}