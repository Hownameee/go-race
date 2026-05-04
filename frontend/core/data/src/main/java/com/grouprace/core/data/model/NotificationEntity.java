package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class NotificationEntity {
    @PrimaryKey
    public int id;
    
    public int userId;
    public String type;
    public Integer actorId;
    public Integer activityId;
    public String title;
    public String message;
    public String createdAt;
    public boolean read;

    public String avtUrl;
}
