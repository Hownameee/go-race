package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.model.Post;
import com.grouprace.core.model.Comment;
import com.grouprace.core.network.model.post.NetworkPost;
import com.grouprace.core.network.model.post.NetworkComment;
import com.grouprace.core.network.model.post.CommentPayload;
import com.grouprace.core.network.source.PostNetworkDataSource;
import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.model.PostEntity;
import com.grouprace.core.common.result.Result;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class PostRepositoryImpl implements PostRepository {

    private final PostNetworkDataSource postNetworkDataSource;
    private final PostDao postDao;

    @Inject
    public PostRepositoryImpl(PostNetworkDataSource postNetworkDataSource, PostDao postDao) {
        this.postNetworkDataSource = postNetworkDataSource;
        this.postDao = postDao;
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
    public LiveData<Result<Boolean>> syncPosts(String cursor, int limit) {
        MutableLiveData<Result<Boolean>> resultData = new MutableLiveData<>();
        resultData.postValue(new Result.Loading<>());

        LiveData<Result<List<NetworkPost>>> networkCall = postNetworkDataSource.getPosts(cursor, limit);
        
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
                            p.getUsername(), p.getDisplayName(), p.getProfilePictureUrl(),
                            p.isLiked()
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
    public LiveData<Result<List<Comment>>> getComments(int postId) {
        return Transformations.map(postNetworkDataSource.getComments(postId), result -> {
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
    public LiveData<Result<Boolean>> createComment(int postId, String content) {
        return postNetworkDataSource.createComment(postId, content);
    }

    @Override
    public LiveData<Result<Boolean>> deleteComment(int postId, int commentId) {
        return postNetworkDataSource.deleteComment(postId, commentId);
    }
}
