package com.grouprace.core.model;

public class ClubEvent {
    private int eventId;
    private int clubId;
    private String title;
    private String description;
    private double targetDistance;
    private String startTime;
    private String endTime;
    private boolean isJoined;
    private double currentDistance;
    private int participantsCount;
    private double globalDistance;

    public ClubEvent() {}

    public ClubEvent(int eventId, int clubId, String title, String description, double targetDistance, String startTime, String endTime, boolean isJoined, double currentDistance, int participantsCount, double globalDistance) {
        this.eventId = eventId;
        this.clubId = clubId;
        this.title = title;
        this.description = description;
        this.targetDistance = targetDistance;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isJoined = isJoined;
        this.currentDistance = currentDistance;
        this.participantsCount = participantsCount;
        this.globalDistance = globalDistance;
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

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public boolean isJoined() { return isJoined; }
    public void setJoined(boolean joined) { isJoined = joined; }

    public double getCurrentDistance() { return currentDistance; }
    public void setCurrentDistance(double currentDistance) { this.currentDistance = currentDistance; }

    public int getParticipantsCount() { return participantsCount; }
    public void setParticipantsCount(int participantsCount) { this.participantsCount = participantsCount; }

    public double getGlobalDistance() { return globalDistance; }
    public void setGlobalDistance(double globalDistance) { this.globalDistance = globalDistance; }
}
