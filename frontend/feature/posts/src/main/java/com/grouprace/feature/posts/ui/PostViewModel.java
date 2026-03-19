package com.grouprace.feature.posts.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.data.repository.PostRepository;
import com.grouprace.core.model.Post;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

import java.util.List;

@HiltViewModel
public class PostViewModel extends ViewModel {

    private final PostRepository postRepository;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> _postStatus = new MutableLiveData<>();
    
    private final MutableLiveData<Void> fetchTrigger = new MutableLiveData<>();
    private final LiveData<List<Post>> posts;

    @Inject
    public PostViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
        
        this.posts = Transformations.switchMap(fetchTrigger, trigger -> {
            return postRepository.getPosts();
        });
        
        // Trigger initial fetch
        fetchPosts();
    }

    public LiveData<Boolean> getIsLoading() { return _isLoading; }
    public LiveData<String> getPostStatus() { return _postStatus; }
    
    // Expose posts to the Fragment
    public LiveData<List<Post>> getPosts() { return posts; }

    public void fetchPosts() {
        _isLoading.setValue(true);
        _postStatus.setValue("Fetching posts...");
        
        // This triggers the switchMap to call postRepository.getPosts() again
        fetchTrigger.setValue(null);
        
        // Since we don't have a callback from the repository for completion right now,
        // we turn off loading for demonstration. Ideally the repository should return a State/Resource wrapper.
        _isLoading.setValue(false);
    }
}
