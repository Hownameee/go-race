package com.grouprace.feature.posts.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

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

    @Inject
    public AddPostViewModel(PostRepository postRepository, RecordRepository recordRepository) {
        this.postRepository = postRepository;
        this.recordRepository = recordRepository;
    }

    public LiveData<List<Record>> getTodayRecords() {
        return recordRepository.getTodayRecords();
    }

    public LiveData<Result<Boolean>> createPost(String title, String description, Integer recordId) {
        return postRepository.createPost(title, description, recordId);
    }
}
