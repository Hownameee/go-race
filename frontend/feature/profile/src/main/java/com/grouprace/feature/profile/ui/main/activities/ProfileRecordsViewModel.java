package com.grouprace.feature.profile.ui.main.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Record;
import com.grouprace.core.network.model.NetworkRecord;
import com.grouprace.core.network.source.RecordNetworkDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileRecordsViewModel extends ViewModel {
    private static final int PAGE_SIZE = 10;

    private final RecordNetworkDataSource recordNetworkDataSource;
    private final MutableLiveData<List<Record>> records = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Result<Boolean>> syncStatus = new MutableLiveData<>();
    private LiveData<Result<List<NetworkRecord>>> currentSource;
    private Observer<Result<List<NetworkRecord>>> currentObserver;
    private int userId = -1;
    private int limit = PAGE_SIZE;

    @Inject
    public ProfileRecordsViewModel(RecordNetworkDataSource recordNetworkDataSource) {
        this.recordNetworkDataSource = recordNetworkDataSource;
    }

    public void initialize(int userId) {
        if (this.userId == userId) {
            return;
        }
        this.userId = userId;
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
        fetchRecords(true);
    }

    private void fetchRecords(boolean appendWindow) {
        if (userId <= 0) {
            return;
        }
        if (currentSource != null && currentObserver != null) {
            currentSource.removeObserver(currentObserver);
        }

        currentSource = recordNetworkDataSource.getUserRecords(userId, 0, limit);
        currentObserver = result -> {
            if (result instanceof Result.Loading) {
                syncStatus.setValue(new Result.Loading<>());
            } else if (result instanceof Result.Success) {
                List<NetworkRecord> networkRecords = ((Result.Success<List<NetworkRecord>>) result).data;
                List<Record> mapped = networkRecords == null
                        ? new ArrayList<>()
                        : networkRecords.stream()
                        .map(NetworkRecord::asExternalModel)
                        .collect(Collectors.toList());
                records.setValue(mapped);
                syncStatus.setValue(new Result.Success<>(true));
            } else if (result instanceof Result.Error) {
                Result.Error<List<NetworkRecord>> error = (Result.Error<List<NetworkRecord>>) result;
                syncStatus.setValue(new Result.Error<>(error.exception, error.message));
            }
        };
        currentSource.observeForever(currentObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentSource != null && currentObserver != null) {
            currentSource.removeObserver(currentObserver);
        }
    }
}
