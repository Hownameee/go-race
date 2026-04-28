package com.grouprace.core.model;

public class ClubAdmin {
    private int userId;
    private String fullname;
    private String avatarUrl;
    private boolean isLeader;

    public ClubAdmin() {}

    public ClubAdmin(int userId, String fullname, String avatarUrl, boolean isLeader) {
        this.userId = userId;
        this.fullname = fullname;
        this.avatarUrl = avatarUrl;
        this.isLeader = isLeader;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public boolean isLeader() { return isLeader; }
    public void setLeader(boolean leader) { isLeader = leader; }
}
