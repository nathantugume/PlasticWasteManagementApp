package com.ugwebstudio.plasticwastemanagementapp.classes;

public class Pickup {
    private String date;
    private boolean notificationEnabled;
    private String selectedChipTypes;
    private String status;
    private String time;
    private String userId;

    private String collector;
    private String scheduledDate;

    private String documentId;

    // Default constructor
    public Pickup() {
        // Default constructor required for Firebase
    }

    // Parameterized constructor
    public Pickup(String date, boolean notificationEnabled, String selectedChipTypes, String status, String time, String userId, String collector, String scheduledDate,String documentId) {
        this.date = date;
        this.notificationEnabled = notificationEnabled;
        this.selectedChipTypes = selectedChipTypes;
        this.status = status;
        this.time = time;
        this.userId = userId;
        this.collector = collector;
        this.scheduledDate = scheduledDate;
        this.documentId = documentId;
    }

    // Getters and setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public String getSelectedChipTypes() {
        return selectedChipTypes;
    }

    public void setSelectedChipTypes(String selectedChipTypes) {
        this.selectedChipTypes = selectedChipTypes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}

