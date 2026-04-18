package com.grouprace.feature.profile.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.SearchRepository;
import com.grouprace.core.data.repository.UserRepository;
import com.grouprace.core.model.Profile.ProfileOverview;
import com.grouprace.core.model.Profile.WeeklyRecordPoint;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.core.network.model.record.RecordStreakResponse;
import com.grouprace.core.network.model.record.RecordWeeklyPointResponse;
import com.grouprace.core.network.model.record.RecordWeeklySummaryResponse;
import com.grouprace.core.network.source.RecordDataSource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserProfileViewModel extends ViewModel {
    public static final String ACTIVITY_RUNNING = "Running";
    public static final String ACTIVITY_WALKING = "Walking";
    private static final int WEEKLY_SUMMARY_WINDOW = 12;

    private final UserRepository userRepository;
    private final SearchRepository searchRepository;
    private final RecordDataSource recordDataSource;
    private final MutableLiveData<Result<ProfileOverview>> profileOverview = new MutableLiveData<>();
    private final MutableLiveData<Result<WeeklyRecordSummary>> weeklySummary = new MutableLiveData<>();
    private final MutableLiveData<Result<RecordProfileStatisticsResponse>> achievementSummary = new MutableLiveData<>();
    private final MutableLiveData<Result<RecordStreakResponse>> streakSummary = new MutableLiveData<>();
    private final MutableLiveData<String> selectedActivityType = new MutableLiveData<>(ACTIVITY_RUNNING);
    private LiveData<Result<ProfileOverview>> currentOverviewSource;
    private Observer<Result<ProfileOverview>> currentOverviewObserver;
    private LiveData<Result<RecordWeeklySummaryResponse>> currentWeeklySummarySource;
    private Observer<Result<RecordWeeklySummaryResponse>> currentWeeklySummaryObserver;
    private LiveData<Result<RecordProfileStatisticsResponse>> currentAchievementSource;
    private Observer<Result<RecordProfileStatisticsResponse>> currentAchievementObserver;
    private LiveData<Result<RecordStreakResponse>> currentStreakSource;
    private Observer<Result<RecordStreakResponse>> currentStreakObserver;
    private int userId = -1;

    @Inject
    public UserProfileViewModel(
            UserRepository userRepository,
            SearchRepository searchRepository,
            RecordDataSource recordDataSource
    ) {
        this.userRepository = userRepository;
        this.searchRepository = searchRepository;
        this.recordDataSource = recordDataSource;
    }

    public void initialize(int userId) {
        if (this.userId == userId) {
            return;
        }
        this.userId = userId;
    }

    public LiveData<Result<ProfileOverview>> getProfileOverview() {
        return profileOverview;
    }

    public LiveData<Result<WeeklyRecordSummary>> getWeeklySummary() {
        return weeklySummary;
    }

    public LiveData<Result<RecordProfileStatisticsResponse>> getAchievementSummary() {
        return achievementSummary;
    }

    public LiveData<Result<RecordStreakResponse>> getStreakSummary() {
        return streakSummary;
    }

    public LiveData<String> getSelectedActivityType() {
        return selectedActivityType;
    }

    public void loadUserOverview() {
        if (userId <= 0) {
            return;
        }
        if (currentOverviewSource != null && currentOverviewObserver != null) {
            currentOverviewSource.removeObserver(currentOverviewObserver);
        }

        currentOverviewSource = userRepository.getUserOverview(userId);
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
        if (userId <= 0) {
            return;
        }
        if (currentWeeklySummarySource != null && currentWeeklySummaryObserver != null) {
            currentWeeklySummarySource.removeObserver(currentWeeklySummaryObserver);
        }

        currentWeeklySummarySource = recordDataSource.getUserWeeklySummary(userId, activityType, WEEKLY_SUMMARY_WINDOW);
        currentWeeklySummaryObserver = result -> {
            if (result instanceof Result.Loading) {
                weeklySummary.setValue(new Result.Loading<>());
            } else if (result instanceof Result.Success) {
                RecordWeeklySummaryResponse response = ((Result.Success<RecordWeeklySummaryResponse>) result).data;
                weeklySummary.setValue(new Result.Success<>(mapToWeeklySummary(response)));
            } else {
                Result.Error<RecordWeeklySummaryResponse> error = (Result.Error<RecordWeeklySummaryResponse>) result;
                weeklySummary.setValue(new Result.Error<>(error.exception, error.message));
            }
        };
        currentWeeklySummarySource.observeForever(currentWeeklySummaryObserver);
    }

    public void loadAchievementSummary() {
        if (userId <= 0) {
            return;
        }
        if (currentAchievementSource != null && currentAchievementObserver != null) {
            currentAchievementSource.removeObserver(currentAchievementObserver);
        }

        currentAchievementSource = recordDataSource.getUserProfileStatistics(userId, null);
        currentAchievementObserver = result -> achievementSummary.setValue(result);
        currentAchievementSource.observeForever(currentAchievementObserver);
    }

    public void loadStreakSummary() {
        if (userId <= 0) {
            return;
        }
        if (currentStreakSource != null && currentStreakObserver != null) {
            currentStreakSource.removeObserver(currentStreakObserver);
        }

        currentStreakSource = recordDataSource.getUserStreak(userId);
        currentStreakObserver = result -> streakSummary.setValue(result);
        currentStreakSource.observeForever(currentStreakObserver);
    }

    public LiveData<Result<Boolean>> followUser() {
        return searchRepository.followUser(userId);
    }

    public LiveData<Result<Boolean>> unfollowUser() {
        return searchRepository.unfollowUser(userId);
    }

    public void applyFollowingState(boolean isFollowing) {
        Result<ProfileOverview> currentResult = profileOverview.getValue();
        if (!(currentResult instanceof Result.Success)) {
            return;
        }

        ProfileOverview overview = ((Result.Success<ProfileOverview>) currentResult).data;
        if (overview == null) {
            return;
        }

        boolean wasFollowing = overview.isFollowing();
        overview.setFollowing(isFollowing);
        if (wasFollowing != isFollowing) {
            int followerCount = overview.getTotalFollowers() + (isFollowing ? 1 : -1);
            overview.setTotalFollowers(Math.max(0, followerCount));
        }
        profileOverview.setValue(new Result.Success<>(overview));
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
        if (currentAchievementSource != null && currentAchievementObserver != null) {
            currentAchievementSource.removeObserver(currentAchievementObserver);
        }
        if (currentStreakSource != null && currentStreakObserver != null) {
            currentStreakSource.removeObserver(currentStreakObserver);
        }
    }

    private WeeklyRecordSummary mapToWeeklySummary(RecordWeeklySummaryResponse response) {
        if (response == null) {
            return null;
        }

        List<WeeklyRecordPoint> points = new ArrayList<>();
        if (response.getPoints() != null) {
            for (RecordWeeklyPointResponse point : response.getPoints()) {
                points.add(new WeeklyRecordPoint(
                        point.getWeekStart(),
                        point.getWeekEnd(),
                        point.getTotalDistanceKm(),
                        point.getTotalDurationSeconds(),
                        point.getTotalElevationGainM()
                ));
            }
        }

        return new WeeklyRecordSummary(response.getActivityType(), response.getWeeks(), points);
    }
}
