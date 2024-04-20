package com.ugwebstudio.plasticwastemanagementapp.classes;

import java.util.Date;

public class Report {
    private String id;
    private String date;
    private double weight;
    private String status;

    public Report() {
    }

    public Report(String id, String date, double weight, String status) {
        this.id = id;
        this.date = date;
        this.weight = weight;
        this.status = status;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
