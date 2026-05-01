package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.model.RecordEntity;
import com.grouprace.core.model.Profile.WeeklyRecordPoint;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;
import com.grouprace.core.model.Record;
import com.grouprace.core.model.TodaySummary;
import com.grouprace.core.network.model.NetworkRecord;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.core.network.model.record.RecordStreakResponse;
import com.grouprace.core.network.model.record.RecordWeeklyPointResponse;
import com.grouprace.core.network.model.record.RecordWeeklySummaryResponse;
import com.grouprace.core.network.source.RecordNetworkDataSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class RecordRepositoryImpl implements RecordRepository {
    private final RecordNetworkDataSource recordNetworkDataSource;
    private final RecordDao recordDao;

    @Inject
    public RecordRepositoryImpl(
            RecordNetworkDataSource recordNetworkDataSource,
            RecordDao recordDao
    ) {
        this.recordNetworkDataSource = recordNetworkDataSource;
        this.recordDao = recordDao;
    }

    @Override
    public LiveData<Result<WeeklyRecordSummary>> getMyWeeklySummary(String activityType, int weeks) {
        return Transformations.map(
                recordNetworkDataSource.getMyWeeklySummary(activityType, weeks),
                this::mapWeeklySummaryResult
        );
    }

    @Override
    public LiveData<Result<WeeklyRecordSummary>> getUserWeeklySummary(int userId, String activityType, int weeks) {
        return Transformations.map(
                recordNetworkDataSource.getUserWeeklySummary(userId, activityType, weeks),
                this::mapWeeklySummaryResult
        );
    }

    @Override
    public LiveData<Result<RecordProfileStatisticsResponse>> getMyProfileStatistics(String activityType) {
        return recordNetworkDataSource.getMyProfileStatistics(activityType);
    }

    @Override
    public LiveData<Result<RecordProfileStatisticsResponse>> getUserProfileStatistics(int userId, String activityType) {
        return recordNetworkDataSource.getUserProfileStatistics(userId, activityType);
    }

    @Override
    public LiveData<Result<RecordStreakResponse>> getMyStreak() {
        return recordNetworkDataSource.getMyStreak();
    }

    @Override
    public LiveData<Result<RecordStreakResponse>> getUserStreak(int userId) {
        return recordNetworkDataSource.getUserStreak(userId);
    }

    @Override
    public void getNetworkRecord(int recordId) {
        LiveData<Result<List<NetworkRecord>>> networkCall = recordNetworkDataSource.getRecord(recordId);

        networkCall.observeForever(result -> {
            if (result instanceof Result.Success) {
                List<NetworkRecord> networkRecords = ((Result.Success<List<NetworkRecord>>) result).data;
                if (networkRecords != null) {
                    List<RecordEntity> entities = networkRecords.stream()
                            .filter(Objects::nonNull)
                            .map(this::mapToEntity)
                            .collect(Collectors.toList());
                    new Thread(() -> recordDao.insertAll(entities)).start();
                }
            }
        });
    }

    @Override
    public LiveData<List<Record>> getLocalRecords(int limit) {
        return Transformations.map(
                recordDao.getRecords(limit),
                entities -> entities.stream()
                        .map(RecordEntity::asExternalModel)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<Result<Boolean>> getNetworkRecords(int offset, int limit) {
        MutableLiveData<Result<Boolean>> resultData = new MutableLiveData<>();
        resultData.postValue(new Result.Loading<>());

        LiveData<Result<List<NetworkRecord>>> networkCall = recordNetworkDataSource.getRecords(offset, limit);
        networkCall.observeForever(result -> syncRecordsToLocal(resultData, result));

        return resultData;
    }

    @Override
    public LiveData<List<Record>> getLocalUserRecords(int userId, int limit) {
        return Transformations.map(
                recordDao.getRecordsByOwner(userId, limit),
                entities -> entities.stream()
                        .map(RecordEntity::asExternalModel)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<Result<Boolean>> syncUserRecords(int userId, int offset, int limit) {
        MutableLiveData<Result<Boolean>> resultData = new MutableLiveData<>();
        resultData.postValue(new Result.Loading<>());

        LiveData<Result<List<NetworkRecord>>> networkCall = recordNetworkDataSource.getUserRecords(userId, offset, limit);
        networkCall.observeForever(result -> syncRecordsToLocal(resultData, result));

        return resultData;
    }

    @Override
    public LiveData<List<Record>> getTodayRecords() {
        String todayPrefix = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return Transformations.map(
                recordDao.getTodayRecords(todayPrefix),
                entities -> entities.stream()
                        .map(RecordEntity::asExternalModel)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<TodaySummary> getTodaySummary() {
        String todayPrefix = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return Transformations.map(
                recordDao.getTodaySummary(todayPrefix),
                entity -> entity != null ? entity.asExternalModel() : new TodaySummary(0, 0, 0.0f)
        );
    }

    private Result<WeeklyRecordSummary> mapWeeklySummaryResult(Result<RecordWeeklySummaryResponse> result) {
        if (result instanceof Result.Loading) {
            return new Result.Loading<>();
        } else if (result instanceof Result.Success) {
            RecordWeeklySummaryResponse response = ((Result.Success<RecordWeeklySummaryResponse>) result).data;
            return new Result.Success<>(mapToWeeklySummary(response));
        }

        Result.Error<RecordWeeklySummaryResponse> error = (Result.Error<RecordWeeklySummaryResponse>) result;
        return new Result.Error<>(error.exception, error.message);
    }

    private void syncRecordsToLocal(
            MutableLiveData<Result<Boolean>> resultData,
            Result<List<NetworkRecord>> result
    ) {
        if (result instanceof Result.Success) {
            List<NetworkRecord> networkRecords = ((Result.Success<List<NetworkRecord>>) result).data;
            if (networkRecords != null) {
                List<RecordEntity> entities = networkRecords.stream()
                        .map(this::mapToEntity)
                        .collect(Collectors.toList());

                new Thread(() -> {
                    recordDao.insertAll(entities);
                    resultData.postValue(new Result.Success<>(true));
                }).start();
            } else {
                resultData.postValue(new Result.Success<>(true));
            }
        } else if (result instanceof Result.Error) {
            Result.Error<?> error = (Result.Error<?>) result;
            resultData.postValue(new Result.Error<>(error.exception, error.message));
        }
    }

    private RecordEntity mapToEntity(NetworkRecord networkRecord) {
        if (networkRecord == null) return null;
        return new RecordEntity(
                networkRecord.getRecordId(),
                networkRecord.getActivityType(),
                networkRecord.getTitle(),
                networkRecord.getStartTime(),
                networkRecord.getEndTime(),
                networkRecord.getOwnerId(),
                networkRecord.getDuration(),
                networkRecord.getDistance(),
                networkRecord.getCalories(),
                networkRecord.getHeartRate(),
                networkRecord.getSpeed(),
                networkRecord.getImageUrl()
        );
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
                        point.getTotalDurationSeconds()
                ));
            }
        }

        return new WeeklyRecordSummary(response.getActivityType(), response.getWeeks(), points);
    }
}
