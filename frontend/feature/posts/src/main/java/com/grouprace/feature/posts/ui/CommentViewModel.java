package com.grouprace.feature.posts.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.PostRepository;
import com.grouprace.core.model.Comment;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CommentViewModel extends ViewModel {

    private final PostRepository postRepository;
    private final MutableLiveData<Integer> postIdTrigger = new MutableLiveData<>();
    private final MutableLiveData<List<Comment>> commentsList = new MutableLiveData<>();
    private final LiveData<Result<List<Comment>>> comments;

    @Inject
    public CommentViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;

        this.comments = Transformations.switchMap(postIdTrigger, id -> postRepository.getComments(id, null, 50));
    }

    public LiveData<Result<List<Comment>>> getComments() {
        return comments;
    }

    public void loadComments(int postId) {
        postIdTrigger.setValue(postId);
    }

    public LiveData<Result<Boolean>> createComment(int postId, String content, Integer parentId) {
        return postRepository.createComment(postId, content, parentId);
    }

    public LiveData<Result<Boolean>> likeComment(int postId, int commentId, boolean like) {
        if (like) {
            return postRepository.likeComment(postId, commentId);
        } else {
            return postRepository.unlikeComment(postId, commentId);
        }
    }

    public LiveData<Result<List<Comment>>> loadReplies(int postId, int commentId) {
        return postRepository.getReplies(postId, commentId, null, 20);
    }
}
