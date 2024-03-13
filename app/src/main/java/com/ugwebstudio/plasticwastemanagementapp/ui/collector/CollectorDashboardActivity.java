package com.ugwebstudio.plasticwastemanagementapp.ui.collector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.ugwebstudio.plasticwastemanagementapp.R;

public class CollectorDashboardActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector_dashboard);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        boolean isFirstLogin = sharedPreferences.getBoolean("isFirstLogin",true);

        if (isFirstLogin){
            startActivity(new Intent(CollectorDashboardActivity.this, GetStartedCollectorActivity.class));
        }




    }
}