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

    /**
     * Triggers a network fetch and syncs with the local database.
     */
    LiveData<Result<Boolean>> syncPosts(String cursor, int limit);

    LiveData<Result<Boolean>> likePost(int postId, int userId);
    LiveData<Result<Boolean>> unlikePost(int postId, int userId);
    
    LiveData<Result<List<Comment>>> getComments(int postId);
    LiveData<Result<Boolean>> createComment(int postId, int userId, String content);
    LiveData<Result<Boolean>> deleteComment(int postId, int commentId, int userId);
}
