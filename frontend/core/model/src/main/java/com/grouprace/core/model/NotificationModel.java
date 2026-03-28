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

    private boolean isRead;
    // Constructor, getters và setters
    public NotificationModel(int id, int userId, String type, Integer actorId, Integer activityId,
                        String title, String message, String createdAt, boolean read) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.actorId = actorId;
        this.activityId = activityId;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = read;

    }

    // getters ...
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public Integer getActorId() {
        return actorId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}