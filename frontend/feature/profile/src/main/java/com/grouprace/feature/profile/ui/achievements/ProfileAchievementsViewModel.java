package com.grouprace.feature.profile.ui.achievements;

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
public class ProfileAchievementsViewModel extends ViewModel {
    private final RecordDataSource recordDataSource;
    private final MutableLiveData<Result<RecordProfileStatisticsResponse>> achievementSummary = new MutableLiveData<>();
    private LiveData<Result<RecordProfileStatisticsResponse>> currentSource;
    private Observer<Result<RecordProfileStatisticsResponse>> currentObserver;
    private boolean isSelf;
    private int userId;
    private boolean initialized;

    @Inject
    public ProfileAchievementsViewModel(RecordDataSource recordDataSource) {
        this.recordDataSource = recordDataSource;
    }

    public void initialize(boolean isSelf, int userId) {
        if (initialized && this.isSelf == isSelf && this.userId == userId) {
            return;
        }
        this.isSelf = isSelf;
        this.userId = userId;
        this.initialized = true;
        loadAchievements();
    }

    public LiveData<Result<RecordProfileStatisticsResponse>> getAchievementSummary() {
        return achievementSummary;
    }

    private void loadAchievements() {
        if (!initialized) {
            return;
        }
        if (currentSource != null && currentObserver != null) {
            currentSource.removeObserver(currentObserver);
        }

        currentSource = isSelf
                ? recordDataSource.getMyProfileStatistics(null)
                : recordDataSource.getUserProfileStatistics(userId, null);
        currentObserver = result -> achievementSummary.setValue(result);
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
