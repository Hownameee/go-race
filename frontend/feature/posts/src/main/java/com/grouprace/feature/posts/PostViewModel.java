package com.grouprace.feature.posts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PostViewModel extends ViewModel {

    // MutableLiveData can be changed, but we keep it PRIVATE so the Fragment can't mess with it.
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> _postStatus = new MutableLiveData<>();

    // Public LiveData that the Fragment will "observe" (read-only)
    public LiveData<Boolean> getIsLoading() { return _isLoading; }
    public LiveData<String> getPostStatus() { return _postStatus; }

    // The Fragment will call this method when the user clicks the button
    public void submitPost(String content) {
        if (content == null || content.trim().isEmpty()) {
            _postStatus.setValue("Post cannot be empty!");
            return;
        }

        // 1. Tell the UI to show a loading spinner
        _isLoading.setValue(true);

        // 2. Here is where you would normally call your Domain UseCase or Repository
        // Example: savePostUseCase.execute(content);

        // Simulating a successful network call for now:
        _isLoading.setValue(false);
        _postStatus.setValue("Post published successfully!");
    }
}