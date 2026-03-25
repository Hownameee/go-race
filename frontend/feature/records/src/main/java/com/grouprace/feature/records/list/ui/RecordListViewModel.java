package com.grouprace.feature.records.list.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.RecordRepository;
import com.grouprace.core.model.Record;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RecordListViewModel extends ViewModel {
    private static final int PAGE_SIZE = 8;
    private final RecordRepository recordRepository;
    private final MutableLiveData<Integer> fetchTrigger = new MutableLiveData<>();
    private final MediatorLiveData<Result<List<Record>>> recordsResult = new MediatorLiveData<>();
    private final List<Record> allRecords = new ArrayList<>();
    private int currentOffset = 0;
    private boolean isLast = false;
    private boolean isInitialLoadCalled = false;

    @Inject
    public RecordListViewModel(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
        setupRecordsStream();
    }

    private void setupRecordsStream() {
        recordsResult.addSource(fetchTrigger, offset -> {
            LiveData<Result<List<Record>>> source = recordRepository.getRecords(offset);
            
            recordsResult.addSource(source, result -> {
                if (result instanceof Result.Loading) {
                    recordsResult.setValue(new Result.Loading<>());
                    return;
                }
                
                if (result instanceof Result.Success) {
                    handleSuccessResponse(offset, ((Result.Success<List<Record>>) result).data);
                } else if (result instanceof Result.Error) {
                    Result.Error<?> error = (Result.Error<?>) result;
                    recordsResult.setValue(new Result.Error<>(error.exception, error.message));
                }
                
                recordsResult.removeSource(source);
            });
        });
    }

    private void handleSuccessResponse(int offset, List<Record> newPage) {
        if (offset == 0) {
            allRecords.clear();
            isLast = false;
        }

        if (newPage != null && !newPage.isEmpty()) {
            allRecords.addAll(newPage);
            currentOffset = allRecords.size();
            isLast = newPage.size() < PAGE_SIZE;
        } else {
            isLast = true;
        }
        
        recordsResult.setValue(new Result.Success<>(new ArrayList<>(allRecords)));
    }

    public void loadMore() {
        if (isLast || (recordsResult.getValue() instanceof Result.Loading)) return;
        fetchTrigger.setValue(currentOffset);
    }

    public void refresh() {
        isLast = false;
        currentOffset = 0;
        isInitialLoadCalled = true;
        fetchTrigger.setValue(0);
    }
    
    public void initialLoad() {
        if (!isInitialLoadCalled) {
            refresh();
        } else if (!allRecords.isEmpty()) {
            recordsResult.setValue(new Result.Success<>(new ArrayList<>(allRecords)));
        }
    }

    public LiveData<Result<List<Record>>> getRecords() {
        return recordsResult;
    }

    public boolean getIsLast() {
        return isLast;
    }
}
