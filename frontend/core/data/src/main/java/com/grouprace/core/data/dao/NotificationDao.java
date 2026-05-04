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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(NotificationEntity entity);

    @androidx.room.Update
    void update(NotificationEntity entity);

    @Query("SELECT read FROM notifications WHERE id = :id")
    Boolean isRead(int id);

    @androidx.room.Transaction
    default void upsertAll(List<NotificationEntity> entities) {
        for (NotificationEntity entity : entities) {
            Boolean localRead = isRead(entity.id);
            if (localRead != null) {
                if (localRead) {
                    entity.read = true;
                }
                update(entity);
            } else {
                insert(entity);
            }
        }
    }

    @Query("DELETE FROM notifications")
    void clearAll();

    @Query("UPDATE notifications SET read = 1 WHERE id = :notificationId")
    void markAsRead(int notificationId);

    @Query("SELECT COUNT(*) FROM notifications WHERE read = 0")
    LiveData<Integer> getUnreadCount();
}
