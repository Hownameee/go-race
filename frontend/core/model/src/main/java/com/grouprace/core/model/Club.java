package com.grouprace.core.model;

import java.util.List;

public class Club {
    private int clubId;
    private String name;
    private String description;
    private String avatarUrl;
    private String privacyType;
    private int leaderId;
    private String leaderName;
    private int memberCount;
    private int postCount;
    private String status;

    public Club() {}

    public Club(int clubId, String name, String description, String avatarUrl, String privacyType, int leaderId, String leaderName, int memberCount, int postCount, String status) {
        this.clubId = clubId;
        this.name = name;
        this.description = description;
        this.avatarUrl = avatarUrl;
        this.privacyType = privacyType;
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.memberCount = memberCount;
        this.postCount = postCount;
        this.status = status;
    }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getPrivacyType() { return privacyType; }
    public void setPrivacyType(String privacyType) { this.privacyType = privacyType; }

    public int getLeaderId() { return leaderId; }
    public void setLeaderId(int leaderId) { this.leaderId = leaderId; }

    public String getLeaderName() { return leaderName; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public int getPostCount() { return postCount; }
    public void setPostCount(int postCount) { this.postCount = postCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
