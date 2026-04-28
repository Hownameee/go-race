package com.grouprace.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.grouprace.core.data.model.NotificationEntity;

import java.util.List;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    LiveData<List<NotificationEntity>> getNotifications();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NotificationEntity> notifications);

    @Query("DELETE FROM notifications")
    void clearAll();

    @Query("UPDATE notifications SET read = 1 WHERE id = :notificationId")
    void markAsRead(int notificationId);
}
