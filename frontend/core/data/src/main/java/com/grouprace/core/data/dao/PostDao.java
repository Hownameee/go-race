package com.grouprace.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.grouprace.core.data.model.PostEntity;
import java.util.List;

@Dao
public interface PostDao {
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    LiveData<List<PostEntity>> getAllPosts();

    @Query("SELECT * FROM posts WHERE clubId = :clubId ORDER BY createdAt DESC")
    LiveData<List<PostEntity>> getAllPostsByClubId(int clubId);

    @Query("SELECT * FROM posts WHERE ownerId = :ownerId ORDER BY createdAt DESC LIMIT :limit")
    LiveData<List<PostEntity>> getPostsByOwner(int ownerId, int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(PostEntity post);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<PostEntity> posts);

    @Query("DELETE FROM posts")
    void deleteAll();
}
