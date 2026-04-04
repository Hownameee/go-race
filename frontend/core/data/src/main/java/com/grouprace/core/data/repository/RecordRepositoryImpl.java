package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.WeeklyRecordPoint;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;
import com.grouprace.core.network.model.record.RecordWeeklyPointResponse;
import com.grouprace.core.network.model.record.RecordWeeklySummaryResponse;
import com.grouprace.core.network.source.RecordDataSource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RecordRepositoryImpl implements RecordRepository {
    private final RecordDataSource recordDataSource;

    @Inject
    public RecordRepositoryImpl(RecordDataSource recordDataSource) {
        this.recordDataSource = recordDataSource;
    }

    @Override
    public LiveData<Result<WeeklyRecordSummary>> getMyWeeklySummary(String activityType, int weeks) {
        LiveData<Result<RecordWeeklySummaryResponse>> networkResult =
                recordDataSource.getMyWeeklySummary(activityType, weeks);

        return Transformations.map(networkResult, result -> {
            if (result instanceof Result.Loading) {
                return new Result.Loading<>();
            } else if (result instanceof Result.Success) {
                RecordWeeklySummaryResponse response =
                        ((Result.Success<RecordWeeklySummaryResponse>) result).data;
                return new Result.Success<>(mapToWeeklySummary(response));
            } else {
                Result.Error<RecordWeeklySummaryResponse> error =
                        (Result.Error<RecordWeeklySummaryResponse>) result;
                return new Result.Error<>(error.exception, error.message);
            }
        });
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
