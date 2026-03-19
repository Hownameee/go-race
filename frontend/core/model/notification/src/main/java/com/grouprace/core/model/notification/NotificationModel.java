package com.grouprace.core.model.notification;
public class NotificationModel {
    private String title;
    private String message;
    private long timeInMillis;

    public NotificationModel(String title, String message, long timeInMillis) {
        this.title = title;
        this.message = message;
        this.timeInMillis = timeInMillis;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTimeInMillis() { return timeInMillis; }
}