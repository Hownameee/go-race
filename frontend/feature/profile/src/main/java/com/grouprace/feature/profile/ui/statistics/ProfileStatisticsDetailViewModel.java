package com.grouprace.feature.profile.ui.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.RecordRepository;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.feature.profile.ui.main.ProfileActivityType;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileStatisticsDetailViewModel extends ViewModel {
    private final RecordRepository recordRepository;
    private final MutableLiveData<Result<RecordProfileStatisticsResponse>> statistics = new MutableLiveData<>();
    private final MutableLiveData<String> selectedActivityType = new MutableLiveData<>(ProfileActivityType.RUNNING);
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
        loadStatistics(selectedActivityType.getValue());
    }

    public LiveData<Result<RecordProfileStatisticsResponse>> getStatistics() {
        return statistics;
    }

    public LiveData<String> getSelectedActivityType() {
        return selectedActivityType;
    }

    public void selectActivityType(String activityType) {
        if (activityType == null) {
            return;
        }
        selectedActivityType.setValue(activityType);
        loadStatistics(activityType);
    }

    private void loadStatistics(String activityType) {
        if (!initialized) {
            return;
        }
        if (currentStatisticsSource != null && currentStatisticsObserver != null) {
            currentStatisticsSource.removeObserver(currentStatisticsObserver);
        }

        currentStatisticsSource = isSelf
                ? recordRepository.getMyProfileStatistics(activityType)
                : recordRepository.getUserProfileStatistics(userId, activityType);
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
