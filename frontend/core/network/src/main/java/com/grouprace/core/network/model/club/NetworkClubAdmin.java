package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;

public class NetworkClubAdmin {
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("fullname")
    private String fullname;
    
    @SerializedName("avatar_url")
    private String avatarUrl;
    
    @SerializedName("is_leader")
    private int isLeader;

    public int getUserId() { return userId; }
    public String getFullname() { return fullname; }
    public String getAvatarUrl() { return avatarUrl; }
    public boolean isLeader() { return isLeader == 1; }
}
