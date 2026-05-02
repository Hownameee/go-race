package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;

public class CreateClubEventRequest {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("target_distance")
    private Double targetDistance;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    public CreateClubEventRequest(String title, String description, Double targetDistance, String startTime, String endTime) {
        this.title = title;
        this.description = description;
        this.targetDistance = targetDistance;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
