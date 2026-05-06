package com.grouprace.feature.profile.ui.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.RecordRepository;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileStatisticsDetailViewModel extends ViewModel {
    /** Only "Running" activities are surfaced (walking is not tracked). */
    private static final String ACTIVITY_TYPE = "Running";

    private final RecordRepository recordRepository;
    private final MutableLiveData<Result<RecordProfileStatisticsResponse>> statistics = new MutableLiveData<>();
    private LiveData<Result<RecordProfileStatisticsResponse>> currentStatisticsSource;
    private Observer<Result<RecordProfileStatisticsResponse>> currentStatisticsObserver;
    private boolean isSelf;
    private int userId;
    private boolean initialized;

    @Inject
    public ProfileStatisticsDetailViewModel(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public void initialize(boolean isSelf, int userId) {
        if (initialized && this.isSelf == isSelf && this.userId == userId) {
            return;
        }
        this.isSelf = isSelf;
        this.userId = userId;
        this.initialized = true;
        loadStatistics();
    }

    public LiveData<Result<RecordProfileStatisticsResponse>> getStatistics() {
        return statistics;
    }

    private void loadStatistics() {
        if (!initialized) {
            return;
        }
        if (currentStatisticsSource != null && currentStatisticsObserver != null) {
            currentStatisticsSource.removeObserver(currentStatisticsObserver);
        }

        currentStatisticsSource = isSelf
                ? recordRepository.getMyProfileStatistics(ACTIVITY_TYPE)
                : recordRepository.getUserProfileStatistics(userId, ACTIVITY_TYPE);
        currentStatisticsObserver = result -> statistics.setValue(result);
        currentStatisticsSource.observeForever(currentStatisticsObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentStatisticsSource != null && currentStatisticsObserver != null) {
            currentStatisticsSource.removeObserver(currentStatisticsObserver);
        }
    }
}
