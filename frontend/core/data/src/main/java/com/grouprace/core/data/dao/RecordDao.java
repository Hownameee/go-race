package com.grouprace.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.grouprace.core.data.model.RecordEntity;

import java.util.List;

@Dao
public interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RecordEntity record);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RecordEntity> records);

    @Query("SELECT * FROM record ORDER BY recordId DESC LIMIT :limit")
    LiveData<List<RecordEntity>> getRecords(int limit);
}
