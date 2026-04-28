package com.grouprace.feature.posts.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.PostRepository;
import com.grouprace.core.model.Post;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MyPostsViewModel extends ViewModel {
    private static final int LIMIT = 20;

    private final PostRepository postRepository;
    private final MutableLiveData<Result<List<Post>>> myPosts = new MutableLiveData<>();
    private LiveData<Result<List<Post>>> currentSource;
    private Observer<Result<List<Post>>> currentObserver;

    @Inject
    public MyPostsViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public LiveData<Result<List<Post>>> getMyPosts() {
        return myPosts;
    }

    public void loadMyPosts() {
        if (currentSource != null && currentObserver != null) {
            currentSource.removeObserver(currentObserver);
        }

        currentSource = postRepository.getMyPosts(null, LIMIT);
        currentObserver = myPosts::setValue;
        currentSource.observeForever(currentObserver);
    }

    public LiveData<Result<Boolean>> likePost(int postId) {
        return postRepository.likePost(postId);
    }

    public LiveData<Result<Boolean>> unlikePost(int postId) {
        return postRepository.unlikePost(postId);
    }

    @Override
    protected void onCleared() {
        if (currentSource != null && currentObserver != null) {
            currentSource.removeObserver(currentObserver);
        }
        super.onCleared();
    }
}
