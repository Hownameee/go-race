package com.grouprace.feature.records.list.ui;

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
    private int currentTopId = 0;
    private int currentBottomId = 0;
    private boolean isFirst = true;

    @Inject
    public RecordsViewModel(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
        records = recordRepository.getRecords();
        this.syncStatus = Transformations.switchMap(syncTrigger, recordRepository::syncRecords);
    }

    public LiveData<Result<Boolean>> getSyncStatus() {
        return syncStatus;
    }

    public LiveData<List<Record>> getRecords() {
        return records;
    }

    public void setTopId(int topId) {
        currentTopId = topId;
        if (isFirst) {
            isFirst = false;
            fetchRecords(currentTopId);
        }
    }

    public void setBottomId(int bottomId) {
        currentBottomId = bottomId;
    }

    public void fetchRecords(int offset) {
        syncTrigger.setValue(offset);
    }
}
