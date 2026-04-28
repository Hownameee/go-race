package com.grouprace.core.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "club_events", primaryKeys = {"eventId"})
public class EventEntity {
    public int eventId;
    public int clubId;
    public String title;
    public String description;
    public double targetDistance;
    public int targetDurationSeconds;
    public String startTime;
    public String endTime;
    public boolean isJoined;
    public double currentDistance;
    public int currentDurationSeconds;
    public int participantsCount;
    public double globalDistance;
    public int globalDurationSeconds;

    public EventEntity(int eventId, int clubId, String title, String description, double targetDistance, int targetDurationSeconds, String startTime, String endTime, boolean isJoined, double currentDistance, int currentDurationSeconds, int participantsCount, double globalDistance, int globalDurationSeconds) {
        this.eventId = eventId;
        this.clubId = clubId;
        this.title = title;
        this.description = description;
        this.targetDistance = targetDistance;
        this.targetDurationSeconds = targetDurationSeconds;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isJoined = isJoined;
        this.currentDistance = currentDistance;
        this.currentDurationSeconds = currentDurationSeconds;
        this.participantsCount = participantsCount;
        this.globalDistance = globalDistance;
        this.globalDurationSeconds = globalDurationSeconds;
    }
}
