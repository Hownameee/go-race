package com.grouprace.feature.profile.ui.main.achievements_preview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.core.network.source.RecordDataSource;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileAchievementsPreviewViewModel extends ViewModel {
    private final RecordDataSource recordDataSource;
    private final MutableLiveData<Result<RecordProfileStatisticsResponse>> statistics = new MutableLiveData<>();
    private LiveData<Result<RecordProfileStatisticsResponse>> statisticsSource;
    private Observer<Result<RecordProfileStatisticsResponse>> statisticsObserver;
    private int userId = -1;
    private boolean self = true;

    @Inject
    public ProfileAchievementsPreviewViewModel(RecordDataSource recordDataSource) {
        this.recordDataSource = recordDataSource;
    }

    public LiveData<Result<RecordProfileStatisticsResponse>> getStatistics() {
        return statistics;
    }

    public void initialize(int userId, boolean self) {
        this.userId = userId;
        this.self = self;
    }

    public void loadStatistics() {
        if (!self && userId <= 0) {
            return;
        }
        if (statisticsSource != null && statisticsObserver != null) {
            statisticsSource.removeObserver(statisticsObserver);
        }

        statisticsSource = self
                ? recordDataSource.getMyProfileStatistics(null)
                : recordDataSource.getUserProfileStatistics(userId, null);
        statisticsObserver = result -> statistics.setValue(result);
        statisticsSource.observeForever(statisticsObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (statisticsSource != null && statisticsObserver != null) {
            statisticsSource.removeObserver(statisticsObserver);
        }
    }
}
