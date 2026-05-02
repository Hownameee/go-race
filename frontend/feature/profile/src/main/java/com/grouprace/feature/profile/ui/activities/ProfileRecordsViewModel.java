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
    private boolean self;

    @Inject
    public ProfileRecordsViewModel(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
        this.records = Transformations.switchMap(limitLiveData, currentLimit -> {
            if (self) {
                return recordRepository.getLocalMyRecords(currentLimit);
            }
            if (userId > 0) {
                return recordRepository.getLocalUserRecords(userId, currentLimit);
            }
            return new MutableLiveData<>();
        });
    }

    public void initialize(int userId, boolean self) {
        if (this.userId == userId && this.self == self) {
            return;
        }
        this.userId = userId;
        this.self = self;
        limitLiveData.setValue(limit);
    }

    public LiveData<List<Record>> getRecords() {
        return records;
    }

    public LiveData<Result<Boolean>> getSyncStatus() {
        return syncStatus;
    }

    public void sync() {
        fetchRecords();
    }

    public void loadMore(int currentListSize) {
        if (currentListSize < limit) {
            return;
        }
        limit += PAGE_SIZE;
        limitLiveData.setValue(limit);
        fetchRecords();
    }

    private void fetchRecords() {
        LiveData<Result<Boolean>> source;
        if (self) {
            source = recordRepository.getNetworkRecords(0, limit);
        } else if (userId > 0) {
            source = recordRepository.syncUserRecords(userId, 0, limit);
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
