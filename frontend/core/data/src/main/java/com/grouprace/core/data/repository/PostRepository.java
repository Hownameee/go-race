package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import com.grouprace.core.model.Post;
import com.grouprace.core.model.Comment;
import com.grouprace.core.common.result.Result;
import java.util.List;

public interface PostRepository {
    /**
     * Observable stream of posts from the local database.
     */
    LiveData<List<Post>> getPosts();
    LiveData<List<Post>> getPostsByClubId(int clubId);
    LiveData<Result<List<Post>>> getMyPosts(String cursor, int limit);

    // ===== Profile Section ====
    LiveData<List<Post>> getLocalMyPosts(int limit);
    LiveData<List<Post>> getLocalUserPosts(int userId, int limit);
    LiveData<Result<List<Post>>> getUserPosts(int userId, String cursor, int limit);
    LiveData<Result<Boolean>> syncMyPosts(String cursor, int limit);
    LiveData<Result<Boolean>> syncUserPosts(int userId, String cursor, int limit);

    /**
     * Triggers a network fetch and syncs with the local database.
     */
    LiveData<Result<Boolean>> syncPosts(String cursor, int limit);
    LiveData<Result<Boolean>> syncClubPosts(int clubId, String cursor, int limit);

    LiveData<Result<Boolean>> likePost(int postId);
    LiveData<Result<Boolean>> unlikePost(int postId);
    
    LiveData<Result<List<Comment>>> getComments(int postId, String cursor, int limit);
    LiveData<Result<Boolean>> createComment(int postId, String content, Integer parentId);
    LiveData<Result<Boolean>> deleteComment(int postId, int commentId);

    LiveData<Result<Boolean>> likeComment(int postId, int commentId);
    LiveData<Result<Boolean>> unlikeComment(int postId, int commentId);
    LiveData<Result<List<Comment>>> getReplies(int postId, int commentId, String cursor, int limit);
    LiveData<Result<Boolean>> createPost(String title, String description, Integer recordId, Integer clubId);
}
