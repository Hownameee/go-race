package com.grouprace.core.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.grouprace.core.data.model.Activity;

import java.util.List;

@Dao
public interface ActivityDao {

    @Insert
    long insert(Activity activity);

    @Update
    void update(Activity activity);

    @Query("SELECT * FROM activities WHERE id = :id")
    Activity getById(long id);

    @Query("SELECT * FROM activities WHERE completed = 1 ORDER BY startTime DESC")
    List<Activity> getAll();
}
