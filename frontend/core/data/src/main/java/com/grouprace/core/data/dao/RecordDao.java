package com.grouprace.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.grouprace.core.data.model.RecordEntity;
import com.grouprace.core.data.model.TodaySummaryEntity;

import java.util.List;

@Dao
public interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RecordEntity record);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RecordEntity> records);

    @Update
    void update(RecordEntity record);

    @Query("SELECT * FROM record WHERE recordId = :id")
    RecordEntity getById(int id);

    @Query("SELECT * FROM record ORDER BY recordId DESC LIMIT :limit")
    LiveData<List<RecordEntity>> getRecords(int limit);

    @Query("SELECT * FROM record WHERE ownerId = :ownerId ORDER BY recordId DESC LIMIT :limit")
    LiveData<List<RecordEntity>> getRecordsByOwner(int ownerId, int limit);

    @Query("SELECT * FROM record WHERE startTime LIKE :todayPrefix || '%' ORDER BY recordId DESC")
    LiveData<List<RecordEntity>> getTodayRecords(String todayPrefix);

    @Query("SELECT COUNT(*) as activityCount, " +
           "COALESCE(SUM(duration), 0) as totalDurationSeconds, " +
           "COALESCE(SUM(distance), 0) as totalDistanceKm " +
           "FROM record WHERE startTime LIKE :todayPrefix || '%'")
    LiveData<TodaySummaryEntity> getTodaySummary(String todayPrefix);
}
