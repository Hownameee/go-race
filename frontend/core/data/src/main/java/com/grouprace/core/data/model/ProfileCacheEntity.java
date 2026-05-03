package com.grouprace.core.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile_cache")
public class ProfileCacheEntity {
    @PrimaryKey
    @NonNull
    public String cacheKey;
    public String json;
    public long updatedAt;

    public ProfileCacheEntity(@NonNull String cacheKey, String json, long updatedAt) {
        this.cacheKey = cacheKey;
        this.json = json;
        this.updatedAt = updatedAt;
    }
}
