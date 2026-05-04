package com.grouprace.core.network.model.post;

import com.google.gson.annotations.SerializedName;

public class CreatePostRequest {

    @SerializedName("owner_id")
    private int ownerId;

    @SerializedName("record_id")
    private Integer recordId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("photo_url")
    private String photoUrl;

    @SerializedName("view_mode")
    private String viewMode;

    @SerializedName("club_id")
    private Integer clubId;

    public CreatePostRequest(Integer recordId, String title, String description, String viewMode, Integer clubId) {
        this.recordId = recordId;
        this.title = title;
        this.description = description;
        this.viewMode = viewMode != null ? viewMode : "Everyone";
        this.clubId = clubId;
    }

    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getViewMode() { return viewMode; }
    public void setViewMode(String viewMode) { this.viewMode = viewMode; }

    public Integer getClubId() { return clubId; }
    public void setClubId(Integer clubId) { this.clubId = clubId; }

    public java.util.Map<String, okhttp3.RequestBody> toPartMap() {
        java.util.Map<String, okhttp3.RequestBody> map = new java.util.HashMap<>();
        if (recordId != null) map.put("record_id", createPartFromString(String.valueOf(recordId)));
        if (title != null) map.put("title", createPartFromString(title));
        if (description != null) map.put("description", createPartFromString(description));
        if (viewMode != null) map.put("view_mode", createPartFromString(viewMode));
        if (clubId != null) map.put("club_id", createPartFromString(String.valueOf(clubId)));
        return map;
    }

    private okhttp3.RequestBody createPartFromString(String string) {
        return okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), string);
    }
}
