package com.grouprace.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.grouprace.core.data.model.ClubEntity;

import java.util.List;

@Dao
public interface ClubDao {
    @Query("SELECT * FROM clubs WHERE status = 'approved'")
    LiveData<List<ClubEntity>> getMyClubs();

    @Query("SELECT * FROM clubs WHERE clubId = :clubId")
    LiveData<ClubEntity> getClubById(int clubId);

    @Query("SELECT * FROM clubs")
    LiveData<List<ClubEntity>> getDiscoverClubs();

    @Query("SELECT * FROM clubs WHERE clubId = :clubId")
    ClubEntity getClubByIdSync(int clubId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertClub(ClubEntity club);

    @androidx.room.Transaction
    default void upsertClubs(List<ClubEntity> clubs) {
        for (ClubEntity newClub : clubs) {
            ClubEntity existing = getClubByIdSync(newClub.clubId);
            if (existing != null) {
                newClub.totalDistance = existing.totalDistance;
                newClub.totalActivities = existing.totalActivities;
                newClub.clubRecordDistanceStr = existing.clubRecordDistanceStr;
                newClub.clubRecordDurationStr = existing.clubRecordDurationStr;
                newClub.personalBestDistanceStr = existing.personalBestDistanceStr;
                newClub.personalBestDurationStr = existing.personalBestDurationStr;
            }
            insertClub(newClub);
        }
    }

    @Query("DELETE FROM clubs WHERE status = 'approved'")
    void deleteAllMyClubs();

    @Query("DELETE FROM clubs WHERE status IS NULL OR status != 'approved'")
    void deleteAllDiscoverClubs();

    @Query("UPDATE clubs SET status = :status WHERE clubId = :clubId")
    void updateStatus(int clubId, String status);

    @Query("UPDATE clubs SET status = NULL WHERE clubId = :clubId")
    void removeStatus(int clubId);

    @Query("UPDATE clubs SET totalDistance = :totalDistance, totalActivities = :totalActivities, clubRecordDistanceStr = :clubRecordDistanceStr, clubRecordDurationStr = :clubRecordDurationStr, personalBestDistanceStr = :personalBestDistanceStr, personalBestDurationStr = :personalBestDurationStr WHERE clubId = :clubId")
    void updateClubStats(int clubId, double totalDistance, int totalActivities, String clubRecordDistanceStr, String clubRecordDurationStr, String personalBestDistanceStr, String personalBestDurationStr);

    @androidx.room.Transaction
    default void replaceLeaderboardAndStats(int clubId, double totalDistance, int totalActivities, String clubRecordDistanceStr, String clubRecordDurationStr, String personalBestDistanceStr, String personalBestDurationStr) {
        updateClubStats(clubId, totalDistance, totalActivities, clubRecordDistanceStr, clubRecordDurationStr, personalBestDistanceStr, personalBestDurationStr);
    }
}
