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
    private final MutableLiveData<Integer> postId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> refreshTrigger = new MutableLiveData<>(true);
    private final LiveData<Result<List<Comment>>> comments;

    @Inject
    public PostDetailViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
        
        // Combine postId and refreshTrigger to load comments
        this.comments = Transformations.switchMap(postId, id -> 
            Transformations.switchMap(refreshTrigger, trigger -> 
                postRepository.getComments(id, null, 100)
            )
        );
    }

    public void setPostId(int id) {
        if (postId.getValue() == null || postId.getValue() != id) {
            postId.setValue(id);
        }
    }

    public LiveData<Post> getPostData() {
        Integer id = postId.getValue();
        if (id == null)
            return new MutableLiveData<>();
        return postRepository.getPostById(id);
    }

    public LiveData<Result<List<Comment>>> getComments() {
        return comments;
    }

    public void loadComments() {
        refreshTrigger.setValue(true);
    }

    public LiveData<Result<Boolean>> likePost(int postId) {
        return postRepository.likePost(postId);
    }

    public LiveData<Result<Boolean>> unlikePost(int postId) {
        return postRepository.unlikePost(postId);
    }

    public LiveData<Result<Boolean>> likeComment(int postId, int commentId, boolean like) {
        if (like) {
            return postRepository.likeComment(postId, commentId);
        } else {
            return postRepository.unlikeComment(postId, commentId);
        }
    }

    public LiveData<Result<Boolean>> createComment(int postId, String content, Integer parentId) {
        return postRepository.createComment(postId, content, parentId);
    }

    public LiveData<Result<List<Comment>>> loadReplies(int postId, int commentId) {
        return postRepository.getReplies(postId, commentId, null, 20);
    }
}
