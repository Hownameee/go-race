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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertClubs(List<ClubEntity> clubs);

    @Query("DELETE FROM clubs WHERE status = 'approved'")
    void deleteAllMyClubs();

    @Query("DELETE FROM clubs WHERE status IS NULL OR status != 'approved'")
    void deleteAllDiscoverClubs();

    @Query("UPDATE clubs SET status = :status WHERE clubId = :clubId")
    void updateStatus(int clubId, String status);

    @Query("UPDATE clubs SET status = NULL WHERE clubId = :clubId")
    void removeStatus(int clubId);
}
