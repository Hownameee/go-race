package com.grouprace.feature.profile.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.RecordRepository;
import com.grouprace.core.data.repository.UserRepository;
import com.grouprace.core.model.Profile.ProfileOverview;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {
    public static final String ACTIVITY_RUNNING = "Running";
    public static final String ACTIVITY_WALKING = "Walking";
    private static final int WEEKLY_SUMMARY_WINDOW = 12;

    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final MutableLiveData<Result<ProfileOverview>> profileOverview = new MutableLiveData<>();
    private final MutableLiveData<Result<WeeklyRecordSummary>> weeklySummary = new MutableLiveData<>();
    private final MutableLiveData<String> selectedActivityType = new MutableLiveData<>(ACTIVITY_RUNNING);
    private LiveData<Result<ProfileOverview>> currentOverviewSource;
    private Observer<Result<ProfileOverview>> currentOverviewObserver;
    private LiveData<Result<WeeklyRecordSummary>> currentWeeklySummarySource;
    private Observer<Result<WeeklyRecordSummary>> currentWeeklySummaryObserver;

    @Inject
    public ProfileViewModel(UserRepository userRepository, RecordRepository recordRepository) {
        this.userRepository = userRepository;
        this.recordRepository = recordRepository;
    }

    public LiveData<Result<ProfileOverview>> getProfileOverview() {
        return profileOverview;
    }

    public LiveData<Result<WeeklyRecordSummary>> getWeeklySummary() {
        return weeklySummary;
    }

    public LiveData<String> getSelectedActivityType() {
        return selectedActivityType;
    }

    public void loadMyOverview() {
        if (currentOverviewSource != null && currentOverviewObserver != null) {
            currentOverviewSource.removeObserver(currentOverviewObserver);
        }

        currentOverviewSource = userRepository.getMyOverview();
        currentOverviewObserver = result -> profileOverview.setValue(result);
        currentOverviewSource.observeForever(currentOverviewObserver);
    }

    public void selectActivityType(String activityType) {
        if (activityType == null) {
            return;
        }

        selectedActivityType.setValue(activityType);
        loadWeeklySummary(activityType);
    }

    public void loadWeeklySummary(String activityType) {
        if (currentWeeklySummarySource != null && currentWeeklySummaryObserver != null) {
            currentWeeklySummarySource.removeObserver(currentWeeklySummaryObserver);
        }

        currentWeeklySummarySource = recordRepository.getMyWeeklySummary(activityType, WEEKLY_SUMMARY_WINDOW);
        currentWeeklySummaryObserver = result -> weeklySummary.setValue(result);
        currentWeeklySummarySource.observeForever(currentWeeklySummaryObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentOverviewSource != null && currentOverviewObserver != null) {
            currentOverviewSource.removeObserver(currentOverviewObserver);
        }
        if (currentWeeklySummarySource != null && currentWeeklySummaryObserver != null) {
            currentWeeklySummarySource.removeObserver(currentWeeklySummaryObserver);
        }
    }
}
