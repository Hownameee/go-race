package com.grouprace.feature.posts.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.PostRepository;
import com.grouprace.core.model.Comment;
import com.grouprace.core.model.Post;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PostDetailViewModel extends ViewModel {

    private final PostRepository postRepository;
    private final MutableLiveData<Integer> postIdTrigger = new MutableLiveData<>();
    private final LiveData<Result<List<Comment>>> comments;
    private final LiveData<Post> postData;

    @Inject
    public PostDetailViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
        this.comments = Transformations.switchMap(postIdTrigger, id -> postRepository.getComments(id, null, 50));
        this.postData = Transformations.switchMap(postIdTrigger, postRepository::getPostById);
    }

    public void setPostId(int postId) {
        if (postIdTrigger.getValue() == null || postIdTrigger.getValue() != postId) {
            postIdTrigger.setValue(postId);
        }
    }
    
    public LiveData<Post> getPostData() {
        return postData;
    }

    public LiveData<Result<List<Comment>>> getComments() {
        return comments;
    }

    public void loadComments() {
        if (postIdTrigger.getValue() != null) {
            postIdTrigger.setValue(postIdTrigger.getValue());
        }
    }

    public LiveData<Result<Boolean>> likePost(int postId) {
        return postRepository.likePost(postId);
    }

    public LiveData<Result<Boolean>> unlikePost(int postId) {
        return postRepository.unlikePost(postId);
    }

    public LiveData<Result<Boolean>> createComment(String content, Integer parentId) {
        Integer postId = postIdTrigger.getValue();
        if (postId == null) return new MutableLiveData<>(new Result.Error<>(null, "Post ID not set"));
        return postRepository.createComment(postId, content, parentId);
    }

    public LiveData<Result<Boolean>> likeComment(int commentId, boolean like) {
        Integer postId = postIdTrigger.getValue();
        if (postId == null) return new MutableLiveData<>(new Result.Error<>(null, "Post ID not set"));
        if (like) {
            return postRepository.likeComment(postId, commentId);
        } else {
            return postRepository.unlikeComment(postId, commentId);
        }
    }

    public LiveData<Result<List<Comment>>> loadReplies(int commentId) {
        Integer postId = postIdTrigger.getValue();
        if (postId == null) return new MutableLiveData<>(new Result.Error<>(null, "Post ID not set"));
        return postRepository.getReplies(postId, commentId, null, 20);
    }
}
