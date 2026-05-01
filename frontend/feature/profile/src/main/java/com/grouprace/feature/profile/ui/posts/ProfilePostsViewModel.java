package com.grouprace.feature.profile.ui.posts;

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
public class ProfilePostsViewModel extends ViewModel {
    private static final int LIMIT = 20;

    private final PostRepository postRepository;
    private final MutableLiveData<Result<List<Post>>> postsResult = new MutableLiveData<>();
    private LiveData<Result<List<Post>>> source;
    private Observer<Result<List<Post>>> sourceObserver;
    private int userId = -1;
    private boolean self;

    @Inject
    public ProfilePostsViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void initialize(int userId, boolean self) {
        this.userId = userId;
        this.self = self;
    }

    public LiveData<Result<List<Post>>> getPostsResult() {
        return postsResult;
    }

    public void sync() {
        if (source != null && sourceObserver != null) {
            source.removeObserver(sourceObserver);
        }

        source = self
                ? postRepository.getMyPosts(null, LIMIT)
                : postRepository.getUserPosts(userId, null, LIMIT);
        sourceObserver = result -> postsResult.setValue(result);
        source.observeForever(sourceObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (source != null && sourceObserver != null) {
            source.removeObserver(sourceObserver);
        }
    }
}
