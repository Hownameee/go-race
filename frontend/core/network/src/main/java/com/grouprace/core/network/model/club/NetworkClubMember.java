package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;

public class NetworkClubMember {
    @SerializedName("userId")
    private int userId;

    @SerializedName("fullname")
    private String fullname;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("role")
    private String role;

    @SerializedName("status")
    private String status;

    @SerializedName("joinedAt")
    private String joinedAt;

    @SerializedName("isLeader")
    private boolean isLeader;

    public int getUserId() { return userId; }
    public String getFullname() { return fullname; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public String getJoinedAt() { return joinedAt; }
    public boolean isLeader() { return isLeader; }
}
