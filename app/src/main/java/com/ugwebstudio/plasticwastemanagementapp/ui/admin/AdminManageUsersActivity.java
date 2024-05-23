package com.ugwebstudio.plasticwastemanagementapp.ui.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.adapters.UserAdapter;
import com.ugwebstudio.plasticwastemanagementapp.classes.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminManageUsersActivity extends AppCompatActivity implements UserAdapter.OnItemClickListener {

    private FirebaseFirestore db;
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private SearchView searchView;
    private BottomNavigationView bottomNavigationView;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_users);

        db = FirebaseFirestore.getInstance();
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        searchView = findViewById(R.id.searchView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading users...");
        progressDialog.setCancelable(false);

        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchUsers();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                userAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.filter(newText);
                return false;
            }
        });

        usersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && bottomNavigationView.isShown()) {
                    bottomNavigationView.setVisibility(BottomNavigationView.GONE);
                } else if (dy < 0) {
                    bottomNavigationView.setVisibility(BottomNavigationView.VISIBLE);
                }
            }
        });
    }

    private void fetchUsers() {
        progressDialog.show(); // Show progress dialog

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss(); // Hide progress dialog

                        List<User> userList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            String id = document.getId();
                            user.setId(id);
                            userList.add(user);
                        }
                        userAdapter = new UserAdapter(userList,this);
                        usersRecyclerView.setAdapter(userAdapter);
                    } else {
                        // Handle the error
                        Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClick(int position, String userId, View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.user_options_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.block_user) {

                // Handle block user action
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Action")
                        .setMessage("Are you sure you want to block this user?")
                        .setPositiveButton("Block", (dialog, which) -> {
                            // Call the delete function here
                            Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Call the block function here

                        })

                        .show();
                return true;
            }
            if (item.getItemId() == R.id.delete_user) {

                // Handle block user action
                // Handle block user action
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Action")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            // Call the delete function here
                            Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Call the block function here

                        })

                        .show();
                return true;
            }


            return false;
        });
        popupMenu.show();
    }

}
