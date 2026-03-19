package com.grouprace.core.model;
public class NotificationModel {
    private String title;
    private String message;

    public NotificationModel(String title, String message) {
        this.title = title;
        this.message = message;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public String getMessage() { return message; }
}