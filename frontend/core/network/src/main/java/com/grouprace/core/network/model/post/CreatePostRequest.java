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

    public CreatePostRequest(int ownerId, String title, String description) {
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.viewMode = "Everyone";
    }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getViewMode() { return viewMode; }
    public void setViewMode(String viewMode) { this.viewMode = viewMode; }
}
