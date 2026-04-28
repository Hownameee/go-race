package com.grouprace.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.grouprace.core.data.model.ClubAdminEntity;

import java.util.List;

@Dao
public interface ClubAdminDao {
    @Query("SELECT * FROM club_admins WHERE clubId = :clubId")
    LiveData<List<ClubAdminEntity>> getAdminsForClub(int clubId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ClubAdminEntity> admins);

    @Query("DELETE FROM club_admins WHERE clubId = :clubId")
    void deleteByClubId(int clubId);

    @Transaction
    default void replaceAdminsForClub(int clubId, List<ClubAdminEntity> admins) {
        deleteByClubId(clubId);
        insertAll(admins);
    }
}
