package com.grouprace.core.model;

public class ClubEvent {
    private int eventId;
    private int clubId;
    private String title;
    private String description;
    private double targetDistance;
    private int targetDurationSeconds;
    private String startTime;
    private String endTime;
    private boolean isJoined;
    private double currentDistance;
    private int currentDurationSeconds;
    private int participantsCount;
    private double globalDistance;
    private int globalDurationSeconds;

    public ClubEvent() {}

    public ClubEvent(int eventId, int clubId, String title, String description, double targetDistance, int targetDurationSeconds, String startTime, String endTime, boolean isJoined, double currentDistance, int currentDurationSeconds, int participantsCount, double globalDistance, int globalDurationSeconds) {
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

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getTargetDistance() { return targetDistance; }
    public void setTargetDistance(double targetDistance) { this.targetDistance = targetDistance; }

    public int getTargetDurationSeconds() { return targetDurationSeconds; }
    public void setTargetDurationSeconds(int targetDurationSeconds) { this.targetDurationSeconds = targetDurationSeconds; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public boolean isJoined() { return isJoined; }
    public void setJoined(boolean joined) { isJoined = joined; }

    public double getCurrentDistance() { return currentDistance; }
    public void setCurrentDistance(double currentDistance) { this.currentDistance = currentDistance; }

    public int getCurrentDurationSeconds() { return currentDurationSeconds; }
    public void setCurrentDurationSeconds(int currentDurationSeconds) { this.currentDurationSeconds = currentDurationSeconds; }

    public int getParticipantsCount() { return participantsCount; }
    public void setParticipantsCount(int participantsCount) { this.participantsCount = participantsCount; }

    public double getGlobalDistance() { return globalDistance; }
    public void setGlobalDistance(double globalDistance) { this.globalDistance = globalDistance; }

    public int getGlobalDurationSeconds() { return globalDurationSeconds; }
    public void setGlobalDurationSeconds(int globalDurationSeconds) { this.globalDurationSeconds = globalDurationSeconds; }
}
