package com.grouprace.core.model;

public class UserSearchResult {
    // ID dùng để gọi API/DB khi người dùng bấm nút "Follow"
    private int userId;

    // Ánh xạ từ cột `fullname` (hoặc `username`) trong bảng USERS
    private String fullname;

    // Ánh xạ từ cột `address`, `nationality` hoặc được tính toán từ backend
    // (VD: "Local Legend near you")
    private String address;

    // Ánh xạ từ cột `avatar_url` trong bảng USERS
    private String avatarUrl;

    // 0: Not Following/Joined, 1: Following/Joined, 2: Requested
    private int followStatus;

    // Constructor đầy đủ
    public UserSearchResult(int userId, String fullname, String subtitle, String avatarUrl, int followStatus) {
        this.userId = userId;
        this.fullname = fullname;
        this.address = subtitle;
        this.avatarUrl = avatarUrl;
        this.followStatus = followStatus;
    }

    // --- Getters và Setters ---

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullname() {
        return fullname;
    }
    public String getAddress() {
        return address;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getFollowStatus() {
        return followStatus;
    }

    public void setFollowStatus(int followStatus) {
        this.followStatus = followStatus;
    }
}