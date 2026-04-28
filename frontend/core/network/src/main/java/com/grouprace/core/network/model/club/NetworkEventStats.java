package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NetworkEventStats {
    @SerializedName("event_id")
    public int eventId;

    @SerializedName("club_id")
    public int clubId;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

    @SerializedName("target_distance")
    public double targetDistance;

    @SerializedName("target_duration_seconds")
    public int targetDurationSeconds;

    @SerializedName("start_time")
    public String startTime;

    @SerializedName("end_time")
    public String endTime;

    @SerializedName("participants_count")
    public int participantsCount;

    @SerializedName("total_distance")
    public double totalDistance;

    @SerializedName("total_duration_seconds")
    public int totalDurationSeconds;

    // maybe separate network call for leaderboard?
    @SerializedName("leaderboard")
    public List<EventLeaderboardEntry> leaderboard;

    public static class EventLeaderboardEntry {
        @SerializedName("member_id")
        public String memberId;

        @SerializedName("member_name")
        public String memberName;

        @SerializedName("avatar_url")
        public String avatarUrl;

        @SerializedName("distance")
        public double distance;

        @SerializedName("duration")
        public int duration;
    }
}
