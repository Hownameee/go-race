package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NetworkClubStats {

    @SerializedName("totalDistance")
    private double totalDistance;

    @SerializedName("totalActivities")
    private int totalActivities;

    @SerializedName("clubRecordDistanceStr")
    private String clubRecordDistanceStr;

    @SerializedName("clubRecordDurationStr")
    private String clubRecordDurationStr;

    @SerializedName("personalBestDistanceStr")
    private String personalBestDistanceStr;

    @SerializedName("personalBestDurationStr")
    private String personalBestDurationStr;

    @SerializedName("leaderboard")
    private List<NetworkLeaderboardEntry> leaderboard;

    public double getTotalDistance() { return totalDistance; }
    public int getTotalActivities() { return totalActivities; }
    public String getClubRecordDistanceStr() { return clubRecordDistanceStr; }
    public String getClubRecordDurationStr() { return clubRecordDurationStr; }
    public String getPersonalBestDistanceStr() { return personalBestDistanceStr; }
    public String getPersonalBestDurationStr() { return personalBestDurationStr; }
    public List<NetworkLeaderboardEntry> getLeaderboard() { return leaderboard; }

    public static class NetworkLeaderboardEntry {
        @SerializedName("memberId")
        private String memberId;

        @SerializedName("memberName")
        private String memberName;

        @SerializedName("avatarUrl")
        private String avatarUrl;

        @SerializedName("distance")
        private double distance;

        public String getMemberId() { return memberId; }
        public String getMemberName() { return memberName; }
        public String getAvatarUrl() { return avatarUrl; }
        public double getDistance() { return distance; }
    }
}
