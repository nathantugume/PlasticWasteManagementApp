package com.ugwebstudio.plasticwastemanagementapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.classes.Pickup;

import java.io.Serializable;
import java.util.List;

public class PickupsAdapter extends RecyclerView.Adapter<PickupsAdapter.ViewHolder> {
    private List<Pickup> pickups ;

    public PickupsAdapter(List<Pickup> pickups) {
        this.pickups = pickups;
        Log.d("PickupsAdapter", "Adapter created with " + pickups.size() + " items.");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickup_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pickup pickup = pickups.get(position);

        String weight = String.valueOf(pickup.getWeight());
        String wei = weight != null ? weight : "0";
        holder.pickupDateTextView.setText(pickup.getDate());
        holder.pickupLocationTextView.setText(pickup.getPickupLocation());
        holder.pickupWeightTextView.setText(wei);
        holder.pickupStatusTextView.setText(pickup.getStatus());
        // Set more data as needed
    }

    @Override
    public int getItemCount() {
        return pickups != null ? pickups.size() : 0;
    }

    public void setPickups(List<Pickup> pickups) {
        this.pickups = pickups;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pickupDateTextView;
        TextView pickupLocationTextView;
        TextView pickupWeightTextView;

        TextView pickupStatusTextView;
        public ViewHolder(View view) {
            super(view);
            pickupDateTextView = view.findViewById(R.id.pickupDateTextView);
            pickupLocationTextView = view.findViewById(R.id.pickupLocationTextView);
            pickupWeightTextView = view.findViewById(R.id.pickWeightTextView);
            pickupStatusTextView = view.findViewById(R.id.pickStatusTextView);
        }
    }
}
