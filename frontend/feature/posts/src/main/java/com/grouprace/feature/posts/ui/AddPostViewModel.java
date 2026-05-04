package com.grouprace.feature.posts.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.PostRepository;
import com.grouprace.core.data.repository.RecordRepository;
import com.grouprace.core.model.Record;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddPostViewModel extends ViewModel {

    private final PostRepository postRepository;
    private final RecordRepository recordRepository;

    private final MutableLiveData<List<String>> _selectedPhotoUrls = new MutableLiveData<>(new java.util.ArrayList<>());
    public LiveData<List<String>> selectedPhotoUrls = _selectedPhotoUrls;

    @Inject
    public AddPostViewModel(PostRepository postRepository, RecordRepository recordRepository) {
        this.postRepository = postRepository;
        this.recordRepository = recordRepository;
    }

    public void addPhoto(String uri) {
        java.util.List<String> current = _selectedPhotoUrls.getValue();
        if (current != null && current.size() < 10) {
            current.add(uri);
            _selectedPhotoUrls.setValue(current);
        }
    }

    public void removePhoto(String uri) {
        java.util.List<String> current = _selectedPhotoUrls.getValue();
        if (current != null) {
            current.remove(uri);
            _selectedPhotoUrls.setValue(current);
        }
    }

    public LiveData<List<Record>> getTodayRecords() {
        return recordRepository.getTodayRecords();
    }

    public LiveData<Result<Boolean>> createPost(String title, String description, Integer recordId, Integer clubId) {
        return postRepository.createPost(title, description, recordId, clubId, _selectedPhotoUrls.getValue());
    }
}
