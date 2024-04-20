package com.ugwebstudio.plasticwastemanagementapp.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ugwebstudio.plasticwastemanagementapp.classes.Report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportsViewModel extends ViewModel {
    private static final String TAG = "ReportsViewModel"; // Log tag for the class
    private MutableLiveData<List<Report>> reportsLiveData = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Report>> getReportsLiveData() {
        return reportsLiveData;
    }

    public void fetchReports(String userID, Date startDate, Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String startDateString = sdf.format(startDate);
        String endDateString = sdf.format(endDate);

        Log.d(TAG, "Starting to fetch reports from " + startDateString + " to " + endDateString);

        db.collection("pickups")
                .whereEqualTo("status","Completed")
                .whereEqualTo("collector",userID)
                .whereGreaterThanOrEqualTo("date", startDateString)
                .whereLessThanOrEqualTo("date", endDateString)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Report> reports = new ArrayList<>();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d(TAG, "No reports found for the specified range.");
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            reports.add(document.toObject(Report.class));
                            Log.d(TAG, "Fetched report: " + document.getId());
                        }
                    }
                    reportsLiveData.setValue(reports);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch reports: " + e.getMessage(), e);
                    reportsLiveData.setValue(null);
                });
    }
}
