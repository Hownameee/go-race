package com.grouprace.core.model;

import java.util.List;

public class EventStats {
    private int eventId;
    private int clubId;
    private String title;
    private String description;
    private double targetDistance;
    private String startTime;
    private String endTime;
    private int participantsCount;
    private double totalDistance;
    private List<ClubStats.LeaderboardEntry> leaderboard;

    public EventStats(int eventId, int clubId, String title, String description, double targetDistance, String startTime, String endTime, int participantsCount, double totalDistance, List<ClubStats.LeaderboardEntry> leaderboard) {
        this.eventId = eventId;
        this.clubId = clubId;
        this.title = title;
        this.description = description;
        this.targetDistance = targetDistance;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participantsCount = participantsCount;
        this.totalDistance = totalDistance;
        this.leaderboard = leaderboard;
    }

    public int getEventId() { return eventId; }
    public int getClubId() { return clubId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getTargetDistance() { return targetDistance; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public int getParticipantsCount() { return participantsCount; }
    public double getTotalDistance() { return totalDistance; }
    public List<ClubStats.LeaderboardEntry> getLeaderboard() { return leaderboard; }
}
