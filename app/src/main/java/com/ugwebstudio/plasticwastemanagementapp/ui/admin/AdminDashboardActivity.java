package com.ugwebstudio.plasticwastemanagementapp.ui.admin;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.ui.LoginActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.topAppBar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.sign_out) {// Handle sign out
                sign_out();
                // Handle other menu items
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        MaterialCardView pickups = findViewById(R.id.admin_pickup_card);
        pickups.setOnClickListener(view -> {
            startActivity(new Intent(AdminDashboardActivity.this,AdminPickupsActivity.class));
        });
        MaterialCardView usersCard = findViewById(R.id.admin_users_card);
        usersCard.setOnClickListener(view -> startActivity(new Intent(AdminDashboardActivity.this,AdminManageUsersActivity.class)));

    }

    private void sign_out() {
        mAuth.signOut();
        startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
    }
}
