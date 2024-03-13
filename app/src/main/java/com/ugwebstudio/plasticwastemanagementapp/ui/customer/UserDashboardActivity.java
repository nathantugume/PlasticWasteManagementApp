package com.ugwebstudio.plasticwastemanagementapp.ui.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;
import com.ugwebstudio.plasticwastemanagementapp.R;

public class UserDashboardActivity extends AppCompatActivity {

    private MaterialCardView schedulePickUpCard, plasticSortingCard, recyclingCard, collectionCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

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
}