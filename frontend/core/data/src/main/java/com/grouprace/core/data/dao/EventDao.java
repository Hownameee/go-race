package com.grouprace.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.grouprace.core.data.model.EventEntity;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM club_events WHERE clubId = :clubId")
    LiveData<List<EventEntity>> getEventsByClubId(int clubId);

    @Query("SELECT * FROM club_events WHERE eventId = :eventId")
    LiveData<EventEntity> getEventById(int eventId);

    @Query("SELECT * FROM club_events WHERE eventId = :eventId")
    EventEntity getEventByIdSync(int eventId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvents(List<EventEntity> events);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(EventEntity event);

    @Query("DELETE FROM club_events WHERE clubId = :clubId")
    void deleteEventsByClubId(int clubId);

    @Transaction
    default void replaceEventsForClub(int clubId, List<EventEntity> events) {
        deleteEventsByClubId(clubId);
        insertEvents(events);
    }
}
