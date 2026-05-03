package com.grouprace.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.grouprace.core.data.model.MyProfileInfoEntity;
import com.grouprace.core.data.model.ProfileCacheEntity;
import com.grouprace.core.data.model.ProfileOverviewEntity;

@Dao
public interface ProfileDao {
    @Query("SELECT * FROM profile_overviews WHERE userId = :userId LIMIT 1")
    LiveData<ProfileOverviewEntity> getOverviewByUserId(int userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertOverview(ProfileOverviewEntity overview);

    @Query("DELETE FROM profile_overviews WHERE selfProfile = 1 AND userId != :userId")
    void clearSelfOverviewExcept(int userId);

    @Query("SELECT * FROM my_profile_info WHERE id = :userId LIMIT 1")
    LiveData<MyProfileInfoEntity> getMyInfo(int userId);

    @Query("SELECT * FROM my_profile_info WHERE id = :userId LIMIT 1")
    MyProfileInfoEntity getMyInfoSync(int userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertMyInfo(MyProfileInfoEntity info);

    @Query("DELETE FROM my_profile_info WHERE id != :userId")
    void clearMyInfoExcept(int userId);

    // ===== Profile Feature Section =====
    @Query("SELECT * FROM profile_cache WHERE cacheKey = :cacheKey LIMIT 1")
    LiveData<ProfileCacheEntity> getProfileCache(String cacheKey);

    @Query("SELECT * FROM profile_cache WHERE cacheKey = :cacheKey LIMIT 1")
    ProfileCacheEntity getProfileCacheSync(String cacheKey);

    // ===== Profile Feature Section =====
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertProfileCache(ProfileCacheEntity cache);

    @Query("DELETE FROM profile_cache WHERE cacheKey = :cacheKey")
    void deleteProfileCache(String cacheKey);
}
