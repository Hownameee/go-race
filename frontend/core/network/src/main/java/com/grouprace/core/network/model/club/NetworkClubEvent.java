package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;

public class NetworkClubEvent {
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

    @SerializedName("start_time")
    public String startTime;

    @SerializedName("end_time")
    public String endTime;

    @SerializedName("is_joined")
    public int isJoined;

    @SerializedName("current_distance")
    public double currentDistance;

    @SerializedName("participants_count")
    public int participantsCount;

    @SerializedName("global_distance")
    public double globalDistance;
}
