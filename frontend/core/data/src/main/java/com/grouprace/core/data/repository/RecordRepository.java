package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;
import com.grouprace.core.model.Record;
import com.grouprace.core.model.TodaySummary;

import java.util.List;

public interface RecordRepository {
    LiveData<Result<WeeklyRecordSummary>> getMyWeeklySummary(String activityType, int weeks);
    void getNetworkRecord(int recordId);
    LiveData<List<Record>> getLocalRecords(int limit);
    LiveData<Result<Boolean>> getNetworkRecords(int offset, int limit);
    LiveData<List<Record>> getTodayRecords();
    LiveData<TodaySummary> getTodaySummary();
    void deleteOldRecords();
}
