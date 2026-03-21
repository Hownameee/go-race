package com.grouprace.core.model;

public class Post {
    private int postId;
    private Integer recordId;
    private int ownerId;
    private String title;
    private String description;
    private String photoUrl;
    private int likeCount;
    private int commentCount;
    private String viewMode;
    private String createdAt;
    private String username;
    private String displayName;
    private String profilePictureUrl;

    public Post() {}

    public Post(int postId, Integer recordId, int ownerId, String title, String description, String photoUrl, int likeCount, int commentCount, String viewMode, String createdAt, String username, String displayName, String profilePictureUrl) {
        this.postId = postId;
        this.recordId = recordId;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.photoUrl = photoUrl;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewMode = viewMode;
        this.createdAt = createdAt;
        this.username = username;
        this.displayName = displayName;
        this.profilePictureUrl = profilePictureUrl;
    }

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
}
