package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;

public class NetworkClub {
    @SerializedName("club_id")
    private int clubId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("privacy_type")
    private String privacyType;

    @SerializedName("leader_id")
    private int leaderId;

    @SerializedName("leader_name")
    private String leaderName;

    @SerializedName("member_count")
    private int memberCount;

    @SerializedName("post_count")
    private int postCount;

    @SerializedName("status")
    private String status;

    public int getClubId() { return clubId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getPrivacyType() { return privacyType; }
    public int getLeaderId() { return leaderId; }
    public String getLeaderName() { return leaderName; }
    public int getMemberCount() { return memberCount; }
    public int getPostCount() { return postCount; }
    public String getStatus() { return status; }
}
