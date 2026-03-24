package com.grouprace.core.model;

public class NotificationModel {
    private int id;
    private int userId;
    private String type;
    private Integer actorId; // nullable
    private Integer activityId; // nullable
    private String title;
    private String message;
    private String createdAt;

    // Constructor, getters và setters
    public NotificationModel(int id, int userId, String type, Integer actorId, Integer activityId,
                        String title, String message, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.actorId = actorId;
        this.activityId = activityId;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
    }

    // getters ...
    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}