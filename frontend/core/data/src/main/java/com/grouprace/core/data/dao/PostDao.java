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

    @Query("SELECT * FROM posts WHERE postId = :postId")
    LiveData<PostEntity> getPostById(int postId);

    @Query("SELECT * FROM posts WHERE ownerId = :ownerId ORDER BY createdAt DESC LIMIT :limit")
    LiveData<List<PostEntity>> getPostsByOwner(int ownerId, int limit);

    // ===== Profile Feature Section =====
    @Query("SELECT * FROM posts WHERE selfOwner = 1 ORDER BY createdAt DESC LIMIT :limit")
    LiveData<List<PostEntity>> getMyPosts(int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(PostEntity post);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<PostEntity> posts);

    @Query("DELETE FROM posts WHERE postId NOT IN (SELECT postId FROM posts ORDER BY createdAt DESC LIMIT 10)")
    void deleteOldPosts();

    @Query("DELETE FROM posts")
    void deleteAll();

    @Query("SELECT * FROM posts WHERE pendingSync = 1")
    List<PostEntity> getPendingPosts();

    @Query("UPDATE posts SET recordId = :realId WHERE recordId = :offlineId")
    void updatePendingPostRecordIds(int offlineId, int realId);

    @Query("DELETE FROM posts WHERE postId = :oldId")
    void deleteById(int oldId);

    @Query("UPDATE posts SET isLiked = :isLiked, likeCount = likeCount + :delta WHERE postId = :postId")
    void updateLikeStatus(int postId, boolean isLiked, int delta);

    @Query("UPDATE posts SET commentCount = commentCount + :delta WHERE postId = :postId")
    void updateCommentCount(int postId, int delta);
}
