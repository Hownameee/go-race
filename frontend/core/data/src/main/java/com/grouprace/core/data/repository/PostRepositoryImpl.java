package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.MediatorLiveData;

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
    private final java.util.concurrent.ExecutorService executorService = java.util.concurrent.Executors.newSingleThreadExecutor();

    @Inject
    public PostRepositoryImpl(PostNetworkDataSource postNetworkDataSource, PostDao postDao, SyncManager syncManager, com.grouprace.core.network.utils.SessionManager sessionManager) {
        this.postNetworkDataSource = postNetworkDataSource;
        this.postDao = postDao;
        this.syncManager = syncManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public LiveData<List<Post>> getPosts() {
        MediatorLiveData<List<Post>> result = new MediatorLiveData<>();
        result.addSource(postDao.getAllPosts(), entities -> {
            executorService.execute(() -> {
                List<Post> posts = entities.stream()
                    .map(PostEntity::asExternalModel)
                    .collect(Collectors.toList());
                result.postValue(posts);
            });
        });
        return result;
    }

    @Override
    public LiveData<List<Post>> getPostsByClubId(int clubId) {
        MediatorLiveData<List<Post>> result = new MediatorLiveData<>();
        result.addSource(postDao.getAllPostsByClubId(clubId), entities -> {
            executorService.execute(() -> {
                List<Post> posts = entities.stream()
                    .map(PostEntity::asExternalModel)
                    .collect(Collectors.toList());
                result.postValue(posts);
            });
        });
        return result;
    }

    @Override
    public LiveData<Post> getPostById(int postId) {
        MediatorLiveData<Post> result = new MediatorLiveData<>();
        result.addSource(postDao.getPostById(postId), entity -> {
            executorService.execute(() -> {
                Post post = entity != null ? entity.asExternalModel() : null;
                result.postValue(post);
            });
        });
        return result;
    }

    @Override
    public LiveData<Result<List<Post>>> getMyPosts(String cursor, int limit) {
        LiveData<Result<List<NetworkPost>>> networkCall = postNetworkDataSource.getMyPosts(cursor, limit);
        MediatorLiveData<Result<List<Post>>> result = new MediatorLiveData<>();
        result.addSource(networkCall, r -> {
            if (r instanceof Result.Success) {
                executorService.execute(() -> {
                    List<Post> posts = ((Result.Success<List<NetworkPost>>) r).data.stream()
                            .map(NetworkPost::asExternalModel)
                            .collect(Collectors.toList());
                    result.postValue(new Result.Success<>(posts));
                });
            } else if (r instanceof Result.Error) {
                Result.Error<List<NetworkPost>> error = (Result.Error<List<NetworkPost>>) r;
                result.postValue(new Result.Error<>(error.exception, error.message));
            } else {
                result.postValue(new Result.Loading<>());
            }
        });
        return result;
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
        MediatorLiveData<Result<Boolean>> resultData = new MediatorLiveData<>();
        resultData.postValue(new Result.Loading<>());
        
        resultData.addSource(networkCall, result -> {
            if (result instanceof Result.Success) {
                executorService.execute(() -> {
                    List<NetworkPost> networkPosts = ((Result.Success<List<NetworkPost>>) result).data;
                    
                    List<PostEntity> entities = networkPosts.stream()
                        .map(np -> {
                            Post p = np.asExternalModel();
                            return new PostEntity(
                                p.getPostId(), p.getRecordId(), p.getOwnerId(), p.getTitle(),
                                p.getDescription(), p.getPhotoUrls(), p.getLikeCount(),
                                p.getCommentCount(), p.getViewMode(), p.getCreatedAt(),
                                p.getUsername(), p.getFullName(), p.getProfilePictureUrl(),
                                p.getActivityType(), p.getDurationSeconds(), p.getDistanceKm(),
                                p.getSpeed(), p.getRecordImageUrl(), p.isLiked(), p.getClubId(),
                                false // pendingSync = false
                            );
                        })
                        .collect(Collectors.toList());

                    postDao.upsertAll(entities);
                    resultData.postValue(new Result.Success<>(true));
                });
            } else if (result instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) result;
                resultData.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return resultData;
    }

    @Override
    public LiveData<Result<Boolean>> likePost(int postId) {
        executorService.execute(() -> postDao.updateLikeStatus(postId, true, 1));
        LiveData<Result<Boolean>> networkCall = postNetworkDataSource.likePost(postId);
        MediatorLiveData<Result<Boolean>> result = new MediatorLiveData<>();
        result.addSource(networkCall, r -> {
            result.setValue(r);
            if (r instanceof Result.Error) {
                executorService.execute(() -> postDao.updateLikeStatus(postId, false, -1));
            }
        });
        return result;
    }

    @Override
    public LiveData<Result<Boolean>> unlikePost(int postId) {
        executorService.execute(() -> postDao.updateLikeStatus(postId, false, -1));
        LiveData<Result<Boolean>> networkCall = postNetworkDataSource.unlikePost(postId);
        MediatorLiveData<Result<Boolean>> result = new MediatorLiveData<>();
        result.addSource(networkCall, r -> {
            result.setValue(r);
            if (r instanceof Result.Error) {
                executorService.execute(() -> postDao.updateLikeStatus(postId, true, 1));
            }
        });
        return result;
    }

    @Override
    public LiveData<Result<List<Comment>>> getComments(int postId, String cursor, int limit) {
        LiveData<Result<CommentPayload>> networkCall = postNetworkDataSource.getComments(postId, cursor, limit);
        MediatorLiveData<Result<List<Comment>>> resultData = new MediatorLiveData<>();
        resultData.addSource(networkCall, result -> {
            if (result instanceof Result.Success) {
                executorService.execute(() -> {
                    CommentPayload payload = ((Result.Success<CommentPayload>) result).data;
                    List<Comment> comments = payload.getComments().stream()
                            .map(NetworkComment::asExternalModel)
                            .collect(Collectors.toList());
                    resultData.postValue(new Result.Success<>(comments));
                });
            } else if (result instanceof Result.Error) {
                Result.Error<CommentPayload> error = (Result.Error<CommentPayload>) result;
                resultData.postValue(new Result.Error<>(error.exception, error.message));
            } else {
                resultData.postValue(new Result.Loading<>());
            }
        });
        return resultData;
    }

    @Override
    public LiveData<Result<Boolean>> createComment(int postId, String content, Integer parentId) {
        LiveData<Result<Boolean>> networkCall = postNetworkDataSource.createComment(postId, content, parentId);
        MediatorLiveData<Result<Boolean>> result = new MediatorLiveData<>();
        result.addSource(networkCall, r -> {
            result.setValue(r);
            if (r instanceof Result.Success) {
                executorService.execute(() -> postDao.updateCommentCount(postId, 1));
            }
        });
        return result;
    }

    @Override
    public LiveData<Result<Boolean>> deleteComment(int postId, int commentId) {
        LiveData<Result<Boolean>> networkCall = postNetworkDataSource.deleteComment(postId, commentId);
        MediatorLiveData<Result<Boolean>> result = new MediatorLiveData<>();
        result.addSource(networkCall, r -> {
            result.setValue(r);
            if (r instanceof Result.Success) {
                executorService.execute(() -> postDao.updateCommentCount(postId, -1));
            }
        });
        return result;
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
        LiveData<Result<CommentPayload>> networkCall = postNetworkDataSource.getReplies(postId, commentId, cursor, limit);
        MediatorLiveData<Result<List<Comment>>> resultData = new MediatorLiveData<>();
        resultData.addSource(networkCall, result -> {
            if (result instanceof Result.Success) {
                executorService.execute(() -> {
                    CommentPayload payload = ((Result.Success<CommentPayload>) result).data;
                    List<Comment> comments = payload.getComments().stream()
                            .map(NetworkComment::asExternalModel)
                            .collect(Collectors.toList());
                    resultData.postValue(new Result.Success<>(comments));
                });
            } else if (result instanceof Result.Error) {
                Result.Error<CommentPayload> error = (Result.Error<CommentPayload>) result;
                resultData.postValue(new Result.Error<>(error.exception, error.message));
            } else {
                resultData.postValue(new Result.Loading<>());
            }
        });
        return resultData;
    }

    @Override
    public LiveData<Result<Boolean>> createPost(String title, String description, Integer recordId, Integer clubId, List<String> photoUrls) {
        MutableLiveData<Result<Boolean>> resultData = new MutableLiveData<>();
        resultData.postValue(new Result.Loading<>());

        // Use a negative ID for offline creation
        int offlineId = - (new Random().nextInt(1000000) + 1);
        
        // Generate ISO 8601 timestamp
        String currentTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(new java.util.Date());
        
        PostEntity offlineEntity = new PostEntity(
                offlineId, recordId, sessionManager.getUserId(), title, description, photoUrls,
                0, 0, "Everyone", currentTime, null, null, null,
                null, null, null, null, null, false, clubId,
                true // pendingSync = true
        );

        executorService.execute(() -> {
            postDao.upsert(offlineEntity);
            syncManager.schedulePostSync();
            resultData.postValue(new Result.Success<>(true));
        });

        return resultData;
    }

    @Override
    public void deleteOldPosts() {
        executorService.execute(() -> postDao.deleteOldPosts());
    }
}
