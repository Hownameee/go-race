package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.gson.Gson;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.dao.ProfileDao;
import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.model.ProfileCacheEntity;
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
    private final com.grouprace.core.network.utils.SessionManager sessionManager;
    // ===== Profile Feature Section =====
    private final ProfileDao profileDao;
    private final Gson gson = new Gson();

    @Inject
    public RecordRepositoryImpl(
            RecordNetworkDataSource recordNetworkDataSource,
            RecordDao recordDao,
            com.grouprace.core.network.utils.SessionManager sessionManager,
            ProfileDao profileDao
    ) {
        this.recordNetworkDataSource = recordNetworkDataSource;
        this.recordDao = recordDao;
        this.sessionManager = sessionManager;
        this.profileDao = profileDao;
    }

    // ===== Profile Feature Section =====
    @Override
    public LiveData<Result<WeeklyRecordSummary>> getMyWeeklySummary(String activityType, int weeks) {
        return getWeeklySummaryOfflineFirst(
                profileCacheKey("weekly", "self", activityType, weeks),
                recordNetworkDataSource.getMyWeeklySummary(activityType, weeks)
        );
    }

    // ===== Profile Feature Section =====
    @Override
    public LiveData<Result<WeeklyRecordSummary>> getUserWeeklySummary(int userId, String activityType, int weeks) {
        return getWeeklySummaryOfflineFirst(
                profileCacheKey("weekly", "user_" + userId, activityType, weeks),
                recordNetworkDataSource.getUserWeeklySummary(userId, activityType, weeks)
        );
    }

    // ===== Profile Feature Section =====
    @Override
    public LiveData<Result<RecordProfileStatisticsResponse>> getMyProfileStatistics(String activityType) {
        return getProfileResponseOfflineFirst(
                profileCacheKey("statistics", "self", activityType, 0),
                recordNetworkDataSource.getMyProfileStatistics(activityType),
                RecordProfileStatisticsResponse.class
        );
    }

    // ===== Profile Feature Section =====
    @Override
    public LiveData<Result<RecordProfileStatisticsResponse>> getUserProfileStatistics(int userId, String activityType) {
        return getProfileResponseOfflineFirst(
                profileCacheKey("statistics", "user_" + userId, activityType, 0),
                recordNetworkDataSource.getUserProfileStatistics(userId, activityType),
                RecordProfileStatisticsResponse.class
        );
    }

    // ===== Profile Feature Section =====
    @Override
    public LiveData<Result<RecordStreakResponse>> getMyStreak() {
        return getProfileResponseOfflineFirst(
                profileCacheKey("streak", "self", null, 0),
                recordNetworkDataSource.getMyStreak(),
                RecordStreakResponse.class
        );
    }

    // ===== Profile Feature Section =====
    @Override
    public LiveData<Result<RecordStreakResponse>> getUserStreak(int userId) {
        return getProfileResponseOfflineFirst(
                profileCacheKey("streak", "user_" + userId, null, 0),
                recordNetworkDataSource.getUserStreak(userId),
                RecordStreakResponse.class
        );
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
    public LiveData<List<Record>> getLocalMyRecords(int limit) {
        int currentUserId = sessionManager.getUserId();
        if (currentUserId <= 0) {
            return new MutableLiveData<>(new ArrayList<>());
        }

        return Transformations.map(
                recordDao.getRecordsByOwner(currentUserId, limit),
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
        String todayPrefix = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        return Transformations.map(
                recordDao.getTodayRecords(todayPrefix),
                entities -> entities.stream()
                        .map(RecordEntity::asExternalModel)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<TodaySummary> getTodaySummary() {
        String todayPrefix = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        return Transformations.map(
                recordDao.getTodaySummary(todayPrefix),
                entity -> entity != null ? entity.asExternalModel() : new TodaySummary(0, 0, 0.0f)
        );
    }

    private void syncRecordsToLocal(
            MutableLiveData<Result<Boolean>> resultData,
            Result<List<NetworkRecord>> result
    ) {
        if (result instanceof Result.Success) {
            List<NetworkRecord> networkRecords = ((Result.Success<List<NetworkRecord>>) result).data;
            if (networkRecords != null) {
                List<RecordEntity> entities = networkRecords.stream()
                        .filter(Objects::nonNull)
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
                networkRecord.getImageUrl(),
                false
        );
    }

    // ===== Profile Feature Section =====
    private LiveData<Result<WeeklyRecordSummary>> getWeeklySummaryOfflineFirst(
            String cacheKey,
            LiveData<Result<RecordWeeklySummaryResponse>> networkResult
    ) {
        MediatorLiveData<Result<WeeklyRecordSummary>> resultData = new MediatorLiveData<>();
        boolean[] hasLocal = { false };

        resultData.addSource(profileDao.getProfileCache(cacheKey), cache -> {
            RecordWeeklySummaryResponse response = readProfileCache(cache, RecordWeeklySummaryResponse.class);
            if (response != null) {
                hasLocal[0] = true;
                resultData.setValue(new Result.Success<>(mapToWeeklySummary(response)));
            }
        });

        resultData.addSource(networkResult, result -> {
            if (result instanceof Result.Loading) {
                if (!hasLocal[0]) {
                    resultData.setValue(new Result.Loading<>());
                }
            } else if (result instanceof Result.Success) {
                RecordWeeklySummaryResponse response = ((Result.Success<RecordWeeklySummaryResponse>) result).data;
                cacheProfileResponse(cacheKey, response);
                resultData.setValue(new Result.Success<>(mapToWeeklySummary(response)));
            } else if (!hasLocal[0]) {
                Result.Error<RecordWeeklySummaryResponse> error = (Result.Error<RecordWeeklySummaryResponse>) result;
                resultData.setValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return resultData;
    }

    // ===== Profile Feature Section =====
    private <T> LiveData<Result<T>> getProfileResponseOfflineFirst(
            String cacheKey,
            LiveData<Result<T>> networkResult,
            Class<T> responseClass
    ) {
        MediatorLiveData<Result<T>> resultData = new MediatorLiveData<>();
        boolean[] hasLocal = { false };

        resultData.addSource(profileDao.getProfileCache(cacheKey), cache -> {
            T cachedResponse = readProfileCache(cache, responseClass);
            if (cachedResponse != null) {
                hasLocal[0] = true;
                resultData.setValue(new Result.Success<>(cachedResponse));
            }
        });

        resultData.addSource(networkResult, result -> {
            if (result instanceof Result.Loading) {
                if (!hasLocal[0]) {
                    resultData.setValue(new Result.Loading<>());
                }
            } else if (result instanceof Result.Success) {
                T response = ((Result.Success<T>) result).data;
                cacheProfileResponse(cacheKey, response);
                resultData.setValue(new Result.Success<>(response));
            } else if (!hasLocal[0]) {
                Result.Error<T> error = (Result.Error<T>) result;
                resultData.setValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return resultData;
    }

    // ===== Profile Feature Section =====
    private <T> T readProfileCache(ProfileCacheEntity cache, Class<T> responseClass) {
        if (cache == null || cache.json == null) {
            return null;
        }

        try {
            return gson.fromJson(cache.json, responseClass);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    // ===== Profile Feature Section =====
    private void cacheProfileResponse(String cacheKey, Object response) {
        if (response == null) {
            return;
        }

        String json = gson.toJson(response);
        new Thread(() -> profileDao.upsertProfileCache(
                new ProfileCacheEntity(cacheKey, json, System.currentTimeMillis())
        )).start();
    }

    // ===== Profile Feature Section =====
    private String profileCacheKey(String section, String owner, String activityType, int weeks) {
        String normalizedActivity = activityType == null || activityType.trim().isEmpty()
                ? "all"
                : activityType.trim().toLowerCase(Locale.US);
        return "profile:" + section + ":" + owner + ":" + normalizedActivity + ":" + weeks;
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

    @Override
    public void deleteOldRecords() {
        recordDao.deleteOldRecords();
    }
}
