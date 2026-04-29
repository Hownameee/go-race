package com.grouprace.feature.profile.ui.activities;

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
public class ProfileRecordsViewModel extends ViewModel {
    private static final int PAGE_SIZE = 10;

    private final RecordRepository recordRepository;
    private final MutableLiveData<Result<Boolean>> syncStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> limitLiveData = new MutableLiveData<>(PAGE_SIZE);
    private final LiveData<List<Record>> records;
    private int userId = -1;
    private int limit = PAGE_SIZE;

    @Inject
    public ProfileRecordsViewModel(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
        this.records = Transformations.switchMap(limitLiveData, currentLimit ->
                userId > 0
                        ? recordRepository.getLocalUserRecords(userId, currentLimit)
                        : new MutableLiveData<>()
        );
    }

    public void initialize(int userId) {
        if (this.userId == userId) {
            return;
        }
        this.userId = userId;
        limitLiveData.setValue(limit);
    }

    public LiveData<List<Record>> getRecords() {
        return records;
    }

    public LiveData<Result<Boolean>> getSyncStatus() {
        return syncStatus;
    }

    public void sync() {
        fetchRecords(false);
    }

    public void loadMore(int currentListSize) {
        if (currentListSize < limit) {
            return;
        }
        limit += PAGE_SIZE;
        limitLiveData.setValue(limit);
        fetchRecords(true);
    }

    private void fetchRecords(boolean appendWindow) {
        if (userId <= 0) {
            return;
        }
        LiveData<Result<Boolean>> source = recordRepository.syncUserRecords(userId, 0, limit);
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
