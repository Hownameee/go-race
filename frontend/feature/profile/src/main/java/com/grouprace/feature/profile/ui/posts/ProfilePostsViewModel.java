package com.grouprace.feature.profile.ui.posts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
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
    private final MutableLiveData<Result<Boolean>> syncStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> limitLiveData = new MutableLiveData<>(LIMIT);
    private final LiveData<List<Post>> posts;
    private int userId = -1;
    private boolean self;

    @Inject
    public ProfilePostsViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
        this.posts = Transformations.switchMap(limitLiveData, currentLimit -> {
            if (self) {
                return postRepository.getLocalMyPosts(currentLimit);
            }
            if (userId > 0) {
                return postRepository.getLocalUserPosts(userId, currentLimit);
            }
            return new MutableLiveData<>();
        });
    }

    public void initialize(int userId, boolean self) {
        this.userId = userId;
        this.self = self;
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public LiveData<Result<Boolean>> getSyncStatus() {
        return syncStatus;
    }

    public void sync() {
        LiveData<Result<Boolean>> source;
        if (self) {
            source = postRepository.syncMyPosts(null, LIMIT);
        } else if (userId > 0) {
            source = postRepository.syncUserPosts(userId, null, LIMIT);
        } else {
            return;
        }

        source.observeForever(result -> {
            if (result instanceof Result.Loading) {
                syncStatus.setValue(new Result.Loading<>());
            } else if (result instanceof Result.Success) {
                syncStatus.setValue(new Result.Success<>(true));
            } else if (result instanceof Result.Error) {
                Result.Error<Boolean> error = (Result.Error<Boolean>) result;
                syncStatus.setValue(new Result.Error<>(error.exception, error.message));
            }
        });
    }
}
