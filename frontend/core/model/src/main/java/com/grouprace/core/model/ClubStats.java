package com.grouprace.core.model;

import java.util.List;

public class ClubStats {

    private double totalDistance;
    private int totalActivities;
    
    private String clubRecordDistanceStr;
    private String clubRecordDurationStr;

    private String personalBestDistanceStr;
    private String personalBestDurationStr;

    private List<LeaderboardEntry> leaderboard;

    public ClubStats(double totalDistance, int totalActivities, String clubRecordDistanceStr, String clubRecordDurationStr, String personalBestDistanceStr, String personalBestDurationStr, List<LeaderboardEntry> leaderboard) {
        this.totalDistance = totalDistance;
        this.totalActivities = totalActivities;
        this.clubRecordDistanceStr = clubRecordDistanceStr;
        this.clubRecordDurationStr = clubRecordDurationStr;
        this.personalBestDistanceStr = personalBestDistanceStr;
        this.personalBestDurationStr = personalBestDurationStr;
        this.leaderboard = leaderboard;
    }

    public double getTotalDistance() { return totalDistance; }
    public int getTotalActivities() { return totalActivities; }
    public String getClubRecordDistanceStr() { return clubRecordDistanceStr; }
    public String getClubRecordDurationStr() { return clubRecordDurationStr; }
    public String getPersonalBestDistanceStr() { return personalBestDistanceStr; }
    public String getPersonalBestDurationStr() { return personalBestDurationStr; }
    public List<LeaderboardEntry> getLeaderboard() { return leaderboard; }

    public static class LeaderboardEntry {
        private String memberId;
        private String memberName;
        private String avatarUrl;
        private double distance;

        public LeaderboardEntry(String memberId, String memberName, String avatarUrl, double distance) {
            this.memberId = memberId;
            this.memberName = memberName;
            this.avatarUrl = avatarUrl;
            this.distance = distance;
        }

        public String getMemberId() { return memberId; }
        public String getMemberName() { return memberName; }
        public String getAvatarUrl() { return avatarUrl; }
        public double getDistance() { return distance; }
    }
}
