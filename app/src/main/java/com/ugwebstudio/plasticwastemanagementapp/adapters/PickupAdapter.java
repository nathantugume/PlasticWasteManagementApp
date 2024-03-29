package com.ugwebstudio.plasticwastemanagementapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.classes.Pickup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickupAdapter extends RecyclerView.Adapter<PickupAdapter.PickupViewHolder> {
    private List<Pickup> pickupList;

    // Constructor and other necessary methods

    static class PickupViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTextView;
        private TextView timeTextView;
        private TextView statusTextView;

        private TextView userNameTextView;
        private TextView scheduledOnTxt;
        private TextView selectedTypeTxt;

        private ImageView optionsImage;

        PickupViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            scheduledOnTxt = itemView.findViewById(R.id.scheduledOnTextView);
            selectedTypeTxt = itemView.findViewById(R.id.selectedTypeTextView);
            optionsImage = itemView.findViewById(R.id.optionsImage);
        }
    }

    @NonNull
    @Override
    public PickupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pickup_item, parent, false);
        return new PickupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PickupViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Pickup pickup = pickupList.get(position);
        holder.dateTextView.setText(pickup.getDate());
        holder.timeTextView.setText(pickup.getTime());
        holder.statusTextView.setText(pickup.getStatus());
        holder.userNameTextView.setText(pickup.getUserId());
        holder.scheduledOnTxt.setText(pickup.getScheduledDate());
        holder.selectedTypeTxt.setText(pickup.getSelectedChipTypes());

        holder.optionsImage.setOnClickListener(view -> {
            // Create an AlertDialog.Builder instance
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            builder.setTitle("Change Status");

            // Define the options to display in the dialog
            final String[] options = {"Yes", "No"};

            // Set the items/options for the dialog and handle item click
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Handle item click
                    String selectedOption = options[i];
                    if (selectedOption.equals("Yes")) {
                        // Perform action if "Yes" is selected
                        // Update the pickup status to "Pending"
                        updatePickupStatus(pickupList.get(position), "Pending", holder.itemView.getContext());
                    } else if (selectedOption.equals("No")) {
                        // Perform action if "No" is selected
                        // Do nothing or add your logic here
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }

    private void updatePickupStatus(Pickup pickup, String pending, Context context) {
        // Update the pickup status and collector in Firestore
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", pending); // Set status to "pending"
        updateData.put("collector", FirebaseAuth.getInstance().getCurrentUser().getUid()); // Set collector to current user's UID

        FirebaseFirestore.getInstance().collection("pickups")
                .document(pickup.getDocumentId()) // Assuming you have a method to retrieve the document ID of the pickup
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated pickup status and collector
                    Toast.makeText(context, "Pickup status updated to pending", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Failed to update pickup status and collector
                    Toast.makeText(context, "Failed to update pickup status. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public int getItemCount() {
        return pickupList.size();
    }

    // Method to update pickup list
    public void setPickupList(List<Pickup> pickupList) {
        this.pickupList = pickupList;
        notifyDataSetChanged();
    }
}

