package com.grouprace.core.network.model.post;

import com.google.gson.annotations.SerializedName;
import com.grouprace.core.model.Post;

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

    @SerializedName("fullname")
    private String fullName;

    @SerializedName("profile_picture_url")
    private String profilePictureUrl;

    @SerializedName("activity_type")
    private String activityType;

    @SerializedName("duration_seconds")
    private Integer durationSeconds;

    @SerializedName("distance_km")
    private Double distanceKm;

    @SerializedName("speed")
    private Double speed;

    @SerializedName("record_image_url")
    private String recordImageUrl;

    @SerializedName("club_id")
    private Integer clubId;

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

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }

    public String getRecordImageUrl() { return recordImageUrl; }
    public void setRecordImageUrl(String recordImageUrl) { this.recordImageUrl = recordImageUrl; }

    public Post asExternalModel() {
        return new Post(
            postId, recordId, ownerId, title, description, photoUrl,
            likeCount, commentCount, viewMode, createdAt, username, fullName, profilePictureUrl,
            activityType, durationSeconds, distanceKm, speed, recordImageUrl, clubId
        );
    }
}
