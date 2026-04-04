package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;

public interface RecordRepository {
    LiveData<Result<WeeklyRecordSummary>> getMyWeeklySummary(String activityType, int weeks);
}
