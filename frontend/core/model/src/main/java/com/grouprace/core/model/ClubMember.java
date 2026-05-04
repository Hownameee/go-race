package com.grouprace.core.model;

public class ClubMember {
    private int userId;
    private String fullname;
    private String avatarUrl;
    private String role;
    private String status;
    private String joinedAt;
    private boolean isLeader;

    public ClubMember() {}

    public ClubMember(int userId, String fullname, String avatarUrl, String role, String status, String joinedAt, boolean isLeader) {
        this.userId = userId;
        this.fullname = fullname;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.status = status;
        this.joinedAt = joinedAt;
        this.isLeader = isLeader;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getJoinedAt() { return joinedAt; }
    public void setJoinedAt(String joinedAt) { this.joinedAt = joinedAt; }

    public boolean isLeader() { return isLeader; }
    public void setLeader(boolean leader) { isLeader = leader; }
}
