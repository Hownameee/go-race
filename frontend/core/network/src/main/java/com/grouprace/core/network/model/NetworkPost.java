package com.grouprace.core.network.model;

import com.google.gson.annotations.SerializedName;

public class NetworkPost {

    @SerializedName("post_id")
    private int postId;

    @SerializedName("record_id")
    private Integer recordId;

    @SerializedName("owner_id")
    private int ownerId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("photo_url")
    private String photoUrl;

    @SerializedName("like_count")
    private int likeCount;

    @SerializedName("comment_count")
    private int commentCount;

    @SerializedName("view_mode")
    private String viewMode;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("username")
    private String username;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("profile_picture_url")
    private String profilePictureUrl;

    public NetworkPost() {}

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public String getViewMode() { return viewMode; }
    public void setViewMode(String viewMode) { this.viewMode = viewMode; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public com.grouprace.core.model.Post asExternalModel() {
        return new com.grouprace.core.model.Post(
            postId, recordId, ownerId, title, description, photoUrl,
            likeCount, commentCount, viewMode, createdAt, username, displayName, profilePictureUrl
        );
    }
}
