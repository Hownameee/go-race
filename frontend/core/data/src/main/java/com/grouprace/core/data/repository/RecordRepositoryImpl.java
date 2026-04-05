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
import com.grouprace.core.network.model.NetworkRecord;
import com.grouprace.core.network.model.record.RecordWeeklyPointResponse;
import com.grouprace.core.network.model.record.RecordWeeklySummaryResponse;
import com.grouprace.core.network.source.RecordDataSource;
import com.grouprace.core.network.source.RecordNetworkDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class RecordRepositoryImpl implements RecordRepository {
    private final RecordDataSource recordDataSource;
    private final RecordNetworkDataSource recordNetworkDataSource;
    private final RecordDao recordDao;

    @Inject
    public RecordRepositoryImpl(
            RecordDataSource recordDataSource,
            RecordNetworkDataSource recordNetworkDataSource,
            RecordDao recordDao
    ) {
        this.recordDataSource = recordDataSource;
        this.recordNetworkDataSource = recordNetworkDataSource;
        this.recordDao = recordDao;
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

    @Override
    public void getNetworkRecord(int recordId) {
        LiveData<Result<List<NetworkRecord>>> networkCall = recordNetworkDataSource.getRecord(recordId);

        networkCall.observeForever(result -> {
            if (result instanceof Result.Success) {
                List<NetworkRecord> networkRecords = ((Result.Success<List<NetworkRecord>>) result).data;

                if (networkRecords != null) {
                    List<RecordEntity> entities = networkRecords.stream()
                            .map(n -> new RecordEntity(
                                    n.getRecordId(),
                                    n.getActivityType(),
                                    n.getTitle(),
                                    n.getStartTime(),
                                    n.getEndTime(),
                                    n.getOwnerId(),
                                    n.getDuration(),
                                    n.getDistance(),
                                    n.getCalories(),
                                    n.getHeartRate(),
                                    n.getSpeed(),
                                    n.getImageUrl()
                            ))
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

        networkCall.observeForever(result -> {
            if (result instanceof Result.Success) {
                List<NetworkRecord> networkRecords = ((Result.Success<List<NetworkRecord>>) result).data;

                if (networkRecords != null) {
                    List<RecordEntity> entities = networkRecords.stream()
                            .map(n -> new RecordEntity(
                                    n.getRecordId(),
                                    n.getActivityType(),
                                    n.getTitle(),
                                    n.getStartTime(),
                                    n.getEndTime(),
                                    n.getOwnerId(),
                                    n.getDuration(),
                                    n.getDistance(),
                                    n.getCalories(),
                                    n.getHeartRate(),
                                    n.getSpeed(),
                                    n.getImageUrl()
                            ))
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
        });

        return resultData;
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
