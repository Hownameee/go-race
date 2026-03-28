package com.grouprace.feature.records.list.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.RecordRepository;
import com.grouprace.core.model.Record;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RecordsViewModel extends ViewModel {
    private final RecordRepository recordRepository;
    private final MutableLiveData<Integer> syncTrigger = new MutableLiveData<>();
    private final LiveData<Result<Boolean>> syncStatus;
    private final LiveData<List<Record>> records;
    private final int LIMIT = 10;
    private final MutableLiveData<Integer> limitLiveData = new MutableLiveData<>(LIMIT);

    @Inject
    public RecordsViewModel(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
        this.records = Transformations.switchMap(limitLiveData, limit -> recordRepository.getRecords(limit));
        this.syncStatus = Transformations.switchMap(syncTrigger, recordRepository::syncRecords);
    }

    public LiveData<Result<Boolean>> getSyncStatus() {
        return syncStatus;
    }

    public LiveData<List<Record>> getRecords() {
        return records;
    }

    public void syncById(int topId) {
        fetchRecords(topId);
    }

    public void fetchRecords(int offset) {
        syncTrigger.setValue(offset);
    }

    public void loadMore(int currentListSize) {
        int currentLimit = limitLiveData.getValue() != null ? limitLiveData.getValue() : LIMIT;
        if (currentListSize < currentLimit) {
            return;
        }
        limitLiveData.setValue(currentLimit + LIMIT);
    }
}
