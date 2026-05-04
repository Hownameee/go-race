package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.grouprace.core.model.ClubAdmin;

@Entity(tableName = "club_admins", primaryKeys = {"clubId", "userId"})
public class ClubAdminEntity {
    public int clubId;
    public int userId;
    public String fullname;
    public String avatarUrl;
    public boolean isLeader;

    public ClubAdminEntity(int clubId, int userId, String fullname, String avatarUrl, boolean isLeader) {
        this.clubId = clubId;
        this.userId = userId;
        this.fullname = fullname;
        this.avatarUrl = avatarUrl;
        this.isLeader = isLeader;
    }

    public ClubAdmin asExternalModel() {
        return new ClubAdmin(userId, fullname, avatarUrl, isLeader);
    }
}
