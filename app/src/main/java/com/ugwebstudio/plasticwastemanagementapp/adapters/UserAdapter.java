package com.ugwebstudio.plasticwastemanagementapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.classes.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private List<User> filteredUserList;
    private OnItemClickListener onItemClickListener;

    public UserAdapter(List<User> userList, OnItemClickListener onItemClickListener) {
        this.userList = userList;
        this.filteredUserList = userList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(itemView, onItemClickListener, userList);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = filteredUserList.get(position);
        holder.fullNameTextView.setText(user.getFullName());
        holder.emailTextView.setText(user.getEmail());
        holder.phoneTextView.setText(user.getPhone());
        holder.accountTypeTextView.setText(user.getAccountType());
    }

    @Override
    public int getItemCount() {
        return filteredUserList.size();
    }

    public void filter(String query) {
        if (query.isEmpty()) {
            filteredUserList = userList;
        } else {
            filteredUserList = userList.stream()
                    .filter(user -> user.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                            user.getEmail().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView;
        TextView emailTextView;
        TextView phoneTextView;
        TextView accountTypeTextView;
        ImageView userOptions;
        List<User> userList;

        public UserViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener, List<User> userList) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            accountTypeTextView = itemView.findViewById(R.id.accountTypeTextView);
            userOptions = itemView.findViewById(R.id.users_options);
            this.userList = userList;

            // Set the OnClickListener for userOptions
            userOptions.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String userId = userList.get(position).getId(); // Assuming getId() returns the user ID
                        onItemClickListener.onItemClick(position, userId,view);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String userId, View view);
    }
}
