package com.grouprace.feature.posts.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.data.repository.PostRepository;
import com.grouprace.core.data.repository.RecordRepository;
import com.grouprace.core.model.Post;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.TodaySummary;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

import java.util.List;

@HiltViewModel
public class PostViewModel extends ViewModel {

    private final PostRepository postRepository;
    private final RecordRepository recordRepository;
    private final MutableLiveData<String> syncTrigger = new MutableLiveData<>();
    private final LiveData<Result<Boolean>> syncStatus;
    private final LiveData<List<Post>> posts;
    private final LiveData<TodaySummary> todaySummary;
    private final int LIMIT = 20;

    @Inject
    public PostViewModel(PostRepository postRepository, RecordRepository recordRepository) {
        this.postRepository = postRepository;
        this.recordRepository = recordRepository;

        this.posts = postRepository.getPosts();
        this.todaySummary = recordRepository.getTodaySummary();

        this.syncStatus = Transformations.switchMap(syncTrigger, cursor ->
            postRepository.syncPosts(cursor, LIMIT)
        );

        fetchPosts(null);
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public LiveData<TodaySummary> getTodaySummary() {
        return todaySummary;
    }

    public LiveData<Result<Boolean>> getSyncStatus() {
        return syncStatus;
    }

    public void fetchPosts(String cursor) {
        syncTrigger.setValue(cursor);
    }

    public LiveData<Result<Boolean>> likePost(int postId) {
        return postRepository.likePost(postId);
    }

    public LiveData<Result<Boolean>> unlikePost(int postId) {
        return postRepository.unlikePost(postId);
    }
}
