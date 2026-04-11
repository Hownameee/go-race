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
    @Query("SELECT * FROM clubs WHERE isJoined = 1")
    LiveData<List<ClubEntity>> getMyClubs();

    @Query("SELECT * FROM clubs WHERE isJoined = 0")
    LiveData<List<ClubEntity>> getDiscoverClubs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertClubs(List<ClubEntity> clubs);

    @Query("DELETE FROM clubs WHERE isJoined = 1")
    void deleteAllMyClubs();

    @Query("DELETE FROM clubs WHERE isJoined = 0")
    void deleteAllDiscoverClubs();
}
