package com.grouprace.core.network.model.notification;

import com.google.gson.annotations.SerializedName;

public class NetworkNotification {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("type")
    private String type;

    @SerializedName("actor_id")
    private Integer actorId;

    @SerializedName("activity_id")
    private Integer activityId;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("read")
    private int read;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("actor_avatar_url")
    private String avtUrl;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isRead() {
        return read == 1;
    }

    public Integer getActorId() {
        return actorId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getAvtUrl() {
        return avtUrl;
    }

    public void setRead(boolean read) {
        this.read = (read == false ? 0 : 1);
    }


}
