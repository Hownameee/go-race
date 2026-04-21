package com.grouprace.core.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "club_leaderboard", primaryKeys = {"clubId", "memberId"})
public class ClubLeaderboardEntity {
    public int clubId;
    @NonNull
    public String memberId;
    public String memberName;
    public String avatarUrl;
    public double distance;

    public ClubLeaderboardEntity(int clubId, @NonNull String memberId, String memberName, String avatarUrl, double distance) {
        this.clubId = clubId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.avatarUrl = avatarUrl;
        this.distance = distance;
    }
}
