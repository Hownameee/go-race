package com.grouprace.feature.profile.ui.main.stats;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;

public interface ProfileStatsOwner {
    LiveData<Result<WeeklyRecordSummary>> getWeeklySummaryLiveData();
    LiveData<String> getSelectedActivityTypeLiveData();
    void onSelectActivityType(String activityType);
}
