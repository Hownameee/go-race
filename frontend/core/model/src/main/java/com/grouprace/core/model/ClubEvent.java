package com.grouprace.core.model;

public class ClubEvent {
    private String eventId;
    private String clubId;
    private String createdBy;
    private String title;
    private String description;
    private String startTime;
    private String endTime;

    public ClubEvent() {}

    public ClubEvent(String eventId, String clubId, String createdBy, String title, String description, String startTime, String endTime) {
        this.eventId = eventId;
        this.clubId = clubId;
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getClubId() { return clubId; }
    public void setClubId(String clubId) { this.clubId = clubId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
