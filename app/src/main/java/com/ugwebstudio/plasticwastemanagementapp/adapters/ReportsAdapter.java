package com.ugwebstudio.plasticwastemanagementapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ugwebstudio.plasticwastemanagementapp.R;
import com.ugwebstudio.plasticwastemanagementapp.classes.Report;

import java.util.List;
import java.util.Locale;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportViewHolder> {
    private List<Report> reports;

    public ReportsAdapter(List<Report> reports) {
        this.reports = reports;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.dateTextView.setText(report.getDate().toString());
        holder.weightTextView.setText(String.format(Locale.US, "%.2f kg", report.getWeight()));
        holder.statusTextView.setText(report.getStatus());
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public void updateData(List<Report> newReports) {

        reports = newReports;
        notifyDataSetChanged();

    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, weightTextView, statusTextView;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
}
