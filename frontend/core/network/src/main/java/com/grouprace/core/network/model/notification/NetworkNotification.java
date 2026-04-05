package com.grouprace.core.network.model.notification;

import com.google.gson.annotations.SerializedName;
//CREATE TABLE IF NOT EXISTS NOTIFICATIONS (
//        id INTEGER PRIMARY KEY AUTOINCREMENT,
//        user_id INTEGER NOT NULL,
//        type TEXT CHECK (type IN ('like','comment','follow','system')) NOT NULL,
//actor_id INTEGER,
//activity_id INTEGER,
//title TEXT NOT NULL,
//message TEXT,
//read INTEGER DEFAULT 0,
//created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//
//FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
//FOREIGN KEY (actor_id) REFERENCES users(user_id) ON DELETE SET NULL
//);

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

    public void setRead(boolean read) {
        this.read = (read == false ? 0 : 1);
    }


}
