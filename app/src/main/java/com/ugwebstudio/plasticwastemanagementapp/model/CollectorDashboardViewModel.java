package com.ugwebstudio.plasticwastemanagementapp.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CollectorDashboardViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Integer> completedCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> upcomingCount = new MutableLiveData<>();
    private final MutableLiveData<PickupDetails> nextPickup = new MutableLiveData<>();
    private MutableLiveData<String> currentPickupId = new MutableLiveData<>();
    private MutableLiveData<Double> totalCompletedWeight = new MutableLiveData<>(); // Change type to Double

    public LiveData<Integer> getCompletedCount() {
        return completedCount;
    }

    public LiveData<Integer> getUpcomingCount() {
        return upcomingCount;
    }

    public LiveData<PickupDetails> getNextPickup() {
        return nextPickup;
    }


    // Method to get LiveData object for observing pickup ID
    public LiveData<String> getCurrentPickupIdLiveData() {
        return currentPickupId;
    }

    // Method to get LiveData object for observing the total weight of completed pickups
    public LiveData<Double> getTotalCompletedWeightLiveData() {
        return totalCompletedWeight;
    }

    // Fetch pickup details including ID and weight
    // Fetch details including ID and total weight for completed pickups
    public void fetchCompletedPickupsTotalWeight(String userID) {
        db.collection("pickups")
                .whereEqualTo("collector", userID)
                .whereEqualTo("status", "Completed")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalWeight = 0.0;
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Number weight = doc.getDouble("weight"); // Ensure your data type in Firestore is compatible
                        if (weight != null) {
                            totalWeight += weight.doubleValue();
                        }
                    }
                    totalCompletedWeight.setValue(totalWeight); // Set the total weight
                })
                .addOnFailureListener(e -> totalCompletedWeight.setValue(null));
    }



    public void fetchCompletedPickups(String userID) {
        Query query = db.collection("pickups").whereEqualTo("status", "Completed").whereEqualTo("collector", userID);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                completedCount.setValue(task.getResult().size());
            } else {
                completedCount.setValue(0);
            }
        });
    }

    public void fetchUpcomingPickups(String userID) {
        Query query = db.collection("pickups").whereEqualTo("status", "Pending")
                .whereEqualTo("collector",userID);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                upcomingCount.setValue(task.getResult().size());
            } else {
                upcomingCount.setValue(0);
            }
        });
    }

    public void fetchNextPickup(String userID) {
        Query query = db.collection("pickups").whereEqualTo("status", "Pending");
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                if (doc.getString("collector").equals(userID)) {
                    PickupDetails details = new PickupDetails(doc.getString("date"), doc.getString("time"), doc.getString("pickupLocation"),doc.getId());
                    nextPickup.setValue(details);
                }
            } else {
                nextPickup.setValue(null);
            }
        });
    }
    public void updatePickup(String pickupId, double weight) {
        db.collection("pickups").document(pickupId)
                .update("status", "Completed", "weight", weight)
                .addOnSuccessListener(aVoid -> Log.d("ViewModel", "Pickup marked as completed!"))
                .addOnFailureListener(e -> Log.e("ViewModel", "Failed to update pickup: " + e.getMessage()));
    }


    public static class PickupDetails {
        public final String date;
        public final String time;
        public final String location;
        public final String pickUpId;

        public PickupDetails(String date, String time, String location, String pickUpId) {
            this.date = date;
            this.time = time;
            this.location = location;
            this.pickUpId = pickUpId;
        }
    }
}
