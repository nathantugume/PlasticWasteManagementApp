package com.ugwebstudio.plasticwastemanagementapp.ui.collector;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.adapters.ReportsAdapter;
import com.ugwebstudio.plasticwastemanagementapp.model.ReportsViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {
    private ReportsViewModel viewModel;
    private RecyclerView reportsRecyclerView;
    private ReportsAdapter adapter;
    private TextView textViewFrom;
    private TextView textViewTo;
    private TextView no_report_found_txt;
    private Calendar fromCalendar = Calendar.getInstance();
    private Calendar toCalendar = Calendar.getInstance();
    private String userID;
    private Context context;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Reports ...");
        progressDialog.setCancelable(false);

        context = getApplicationContext();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(view -> onBackPressed());

        viewModel = new ViewModelProvider(this).get(ReportsViewModel.class);
        reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        adapter = new ReportsAdapter(new ArrayList<>());
        reportsRecyclerView.setAdapter(adapter);
        no_report_found_txt = findViewById(R.id.no_report_found_txt);


        // Initialize TextViews for date picking
        textViewFrom = findViewById(R.id.textViewFrom);
        textViewTo = findViewById(R.id.textViewTo);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("UID","");
        // Set initial dates on TextViews
        updateLabel();

        // Set click listeners for date pickers
        textViewFrom.setOnClickListener(v -> showDatePickerDialog(true));
        textViewTo.setOnClickListener(v -> showDatePickerDialog(false));

        // Observe ViewModel for changes in reports data
        viewModel.getReportsLiveData().observe(this, reports -> {
            if (reports != null) {
                adapter.updateData(reports);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_nav_collections){
                startActivity(new Intent(this, UpcomingCollectionsActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.bottom_nav_pickups){
                startActivity(new Intent(this, UpcomingCollectionsActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.bottom_nav_reports){
                startActivity(new Intent(this, ReportsActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.bottom_nav_home){
                startActivity(new Intent(this, CollectorDashboardActivity.class));
                return true;
            }
            return false;
        });
    }





    private void showDatePickerDialog(boolean isFrom) {
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // Set the calendar to the new date chosen
            Calendar calendar = isFrom ? fromCalendar : toCalendar;
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();

            // Fetch reports when both dates are set
            fetchReports();
        };

        Calendar activeCalendar = isFrom ? fromCalendar : toCalendar;
        new DatePickerDialog(this, date,
                activeCalendar.get(Calendar.YEAR),
                activeCalendar.get(Calendar.MONTH),
                activeCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        textViewFrom.setText(sdf.format(fromCalendar.getTime()));
        textViewTo.setText(sdf.format(toCalendar.getTime()));
        // Initially fetch reports based on the current dates
        fetchReports();
    }

    private void fetchReports() {
        // Example user ID and conversion of dates from Calendar to Date type
        progressDialog.show();
        Date startDate = fromCalendar.getTime();
        Date endDate = toCalendar.getTime();
        viewModel.fetchReports(userID, startDate, endDate,no_report_found_txt,progressDialog);
    }
}
