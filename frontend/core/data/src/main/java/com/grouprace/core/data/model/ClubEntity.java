package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.grouprace.core.model.Club;

@Entity(tableName = "clubs")
public class ClubEntity {
    @PrimaryKey
    public int clubId;
    public String name;
    public String description;
    public String privacyType;
    public int leaderId;
    public String leaderName;
    public int memberCount;
    public int postCount;
    public String avatarUrl;
    public boolean isJoined;

    public ClubEntity(int clubId, String name, String description, String privacyType, int leaderId, String leaderName, int memberCount, int postCount, String avatarUrl, boolean isJoined) {
        this.clubId = clubId;
        this.name = name;
        this.description = description;
        this.privacyType = privacyType;
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.memberCount = memberCount;
        this.postCount = postCount;
        this.avatarUrl = avatarUrl;
        this.isJoined = isJoined;
    }

    public Club asExternalModel() {
        Club club = new Club(clubId, name, description, avatarUrl, privacyType, leaderId, leaderName, memberCount, postCount, isJoined);
        return club;
    }
}
