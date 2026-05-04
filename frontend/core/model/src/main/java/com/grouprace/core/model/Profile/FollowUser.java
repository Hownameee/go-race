package com.grouprace.core.model.Profile;

public class FollowUser {
    private final int userId;
    private final String username;
    private final String fullname;
    private final String avatarUrl;
    private final String createdAt;

    public FollowUser(int userId, String username, String fullname, String avatarUrl, String createdAt) {
        this.userId = userId;
        this.username = username;
        this.fullname = fullname;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
