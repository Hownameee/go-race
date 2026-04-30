package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.model.Post;
import com.grouprace.core.model.Comment;
import com.grouprace.core.network.model.post.CreatePostRequest;
import com.grouprace.core.network.model.post.NetworkPost;
import com.grouprace.core.network.model.post.NetworkComment;
import com.grouprace.core.network.model.post.CommentPayload;
import com.grouprace.core.network.source.PostNetworkDataSource;
import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.model.PostEntity;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.SyncManager;

import javax.inject.Inject;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PostRepositoryImpl implements PostRepository {

    private final PostNetworkDataSource postNetworkDataSource;
    private final PostDao postDao;
    private final SyncManager syncManager;
    private final com.grouprace.core.network.utils.SessionManager sessionManager;

    @Inject
    public PostRepositoryImpl(PostNetworkDataSource postNetworkDataSource, PostDao postDao, SyncManager syncManager, com.grouprace.core.network.utils.SessionManager sessionManager) {
        this.postNetworkDataSource = postNetworkDataSource;
        this.postDao = postDao;
        this.syncManager = syncManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public LiveData<List<Post>> getPosts() {
        return Transformations.map(postDao.getAllPosts(), entities -> 
            entities.stream()
                .map(PostEntity::asExternalModel)
                .collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<List<Post>> getPostsByClubId(int clubId) {
        return Transformations.map(postDao.getAllPostsByClubId(clubId), entities ->
                entities.stream()
                        .map(PostEntity::asExternalModel)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<Result<List<Post>>> getMyPosts(String cursor, int limit) {
        return Transformations.map(postNetworkDataSource.getMyPosts(cursor, limit), result -> {
            if (result instanceof Result.Success) {
                List<Post> posts = ((Result.Success<List<NetworkPost>>) result).data.stream()
                        .map(NetworkPost::asExternalModel)
                        .collect(Collectors.toList());
                return new Result.Success<>(posts);
            } else if (result instanceof Result.Error) {
                Result.Error<List<NetworkPost>> error = (Result.Error<List<NetworkPost>>) result;
                return new Result.Error<>(error.exception, error.message);
            }
            return new Result.Loading<>();
        });
    }

    @Override
    public LiveData<Result<Boolean>> syncPosts(String cursor, int limit) {
        return syncInternal(postNetworkDataSource.getPosts(cursor, limit));
    }

    @Override
    public LiveData<Result<Boolean>> syncClubPosts(int clubId, String cursor, int limit) {
        return syncInternal(postNetworkDataSource.getClubPosts(clubId, cursor, limit));
    }

    private LiveData<Result<Boolean>> syncInternal(LiveData<Result<List<NetworkPost>>> networkCall) {
        MutableLiveData<Result<Boolean>> resultData = new MutableLiveData<>();
        resultData.postValue(new Result.Loading<>());
        
        networkCall.observeForever(result -> {
            if (result instanceof Result.Success) {
                List<NetworkPost> networkPosts = ((Result.Success<List<NetworkPost>>) result).data;
                
                List<PostEntity> entities = networkPosts.stream()
                    .map(np -> {
                        Post p = np.asExternalModel();
                        return new PostEntity(
                            p.getPostId(), p.getRecordId(), p.getOwnerId(), p.getTitle(),
                            p.getDescription(), p.getPhotoUrl(), p.getLikeCount(),
                            p.getCommentCount(), p.getViewMode(), p.getCreatedAt(),
                            p.getUsername(), p.getFullName(), p.getProfilePictureUrl(),
                            p.getActivityType(), p.getDurationSeconds(), p.getDistanceKm(),
                            p.getSpeed(), p.getRecordImageUrl(), p.isLiked(), p.getClubId(),
                            false // pendingSync = false
                        );
                    })
                    .collect(Collectors.toList());

                new Thread(() -> {
                    postDao.upsertAll(entities);
                    resultData.postValue(new Result.Success<>(true));
                }).start();

            } else if (result instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) result;
                resultData.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return resultData;
    }

    @Override
    public LiveData<Result<Boolean>> likePost(int postId) {
        return postNetworkDataSource.likePost(postId);
    }

    @Override
    public LiveData<Result<Boolean>> unlikePost(int postId) {
        return postNetworkDataSource.unlikePost(postId);
    }

    @Override
    public LiveData<Result<List<Comment>>> getComments(int postId, String cursor, int limit) {
        return Transformations.map(postNetworkDataSource.getComments(postId, cursor, limit), result -> {
            if (result instanceof Result.Success) {
                CommentPayload payload = ((Result.Success<CommentPayload>) result).data;
                List<Comment> comments = payload.getComments().stream()
                        .map(NetworkComment::asExternalModel)
                        .collect(Collectors.toList());
                return new Result.Success<>(comments);
            } else if (result instanceof Result.Error) {
                Result.Error<CommentPayload> error = (Result.Error<CommentPayload>) result;
                return new Result.Error<>(error.exception, error.message);
            } else {
                return new Result.Loading<>();
            }
        });
    }

    @Override
    public LiveData<Result<Boolean>> createComment(int postId, String content, Integer parentId) {
        return postNetworkDataSource.createComment(postId, content, parentId);
    }

    @Override
    public LiveData<Result<Boolean>> deleteComment(int postId, int commentId) {
        return postNetworkDataSource.deleteComment(postId, commentId);
    }

    @Override
    public LiveData<Result<Boolean>> likeComment(int postId, int commentId) {
        return postNetworkDataSource.likeComment(postId, commentId);
    }

    @Override
    public LiveData<Result<Boolean>> unlikeComment(int postId, int commentId) {
        return postNetworkDataSource.unlikeComment(postId, commentId);
    }

    @Override
    public LiveData<Result<List<Comment>>> getReplies(int postId, int commentId, String cursor, int limit) {
        return Transformations.map(postNetworkDataSource.getReplies(postId, commentId, cursor, limit), result -> {
            if (result instanceof Result.Success) {
                CommentPayload payload = ((Result.Success<CommentPayload>) result).data;
                List<Comment> comments = payload.getComments().stream()
                        .map(NetworkComment::asExternalModel)
                        .collect(Collectors.toList());
                return new Result.Success<>(comments);
            } else if (result instanceof Result.Error) {
                Result.Error<CommentPayload> error = (Result.Error<CommentPayload>) result;
                return new Result.Error<>(error.exception, error.message);
            } else {
                return new Result.Loading<>();
            }
        });
    }

    @Override
    public LiveData<Result<Boolean>> createPost(String title, String description, Integer recordId, Integer clubId) {
        MutableLiveData<Result<Boolean>> resultData = new MutableLiveData<>();
        resultData.postValue(new Result.Loading<>());

        // Use a negative ID for offline creation
        int offlineId = - (new Random().nextInt(1000000) + 1);
        
        // Generate ISO 8601 timestamp
        String currentTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(new java.util.Date());
        
        PostEntity offlineEntity = new PostEntity(
                offlineId, recordId, sessionManager.getUserId(), title, description, null,
                0, 0, "PUBLIC", currentTime, null, null, null,
                null, null, null, null, null, false, clubId,
                true // pendingSync = true
        );

        new Thread(() -> {
            postDao.upsert(offlineEntity);
            syncManager.schedulePostSync();
            resultData.postValue(new Result.Success<>(true));
        }).start();

        return resultData;
    }
}
