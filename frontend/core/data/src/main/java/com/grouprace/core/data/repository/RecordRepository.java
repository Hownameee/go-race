package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;
import com.grouprace.core.model.Record;
import com.grouprace.core.model.TodaySummary;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.core.network.model.record.RecordStreakResponse;

import java.util.List;

public interface RecordRepository {
    // ===== Profile Feature Section =====
    LiveData<Result<WeeklyRecordSummary>> getMyWeeklySummary(String activityType, int weeks);

    // ===== Profile Feature Section =====
    LiveData<Result<WeeklyRecordSummary>> getUserWeeklySummary(int userId, String activityType, int weeks);

    // ===== Profile Feature Section =====
    LiveData<Result<RecordProfileStatisticsResponse>> getMyProfileStatistics(String activityType);

    // ===== Profile Feature Section =====
    LiveData<Result<RecordProfileStatisticsResponse>> getUserProfileStatistics(int userId, String activityType);

    // ===== Profile Feature Section =====
    LiveData<Result<RecordStreakResponse>> getMyStreak();

    // ===== Profile Feature Section =====
    LiveData<Result<RecordStreakResponse>> getUserStreak(int userId);

    void getNetworkRecord(int recordId);

    LiveData<List<Record>> getLocalRecords(int limit);

    LiveData<Result<Boolean>> getNetworkRecords(int offset, int limit);

    LiveData<List<Record>> getLocalUserRecords(int userId, int limit);

    LiveData<Result<Boolean>> syncUserRecords(int userId, int offset, int limit);

    LiveData<List<Record>> getTodayRecords();

    LiveData<TodaySummary> getTodaySummary();
}
