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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(PostEntity post);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<PostEntity> posts);

    @Query("DELETE FROM posts WHERE postId NOT IN (SELECT postId FROM posts ORDER BY createdAt DESC LIMIT 10)")
    void deleteOldPosts();

    @Query("DELETE FROM posts")
    void deleteAll();
}
