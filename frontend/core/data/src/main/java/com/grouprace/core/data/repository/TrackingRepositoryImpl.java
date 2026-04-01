package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.model.RecordEntity;
import com.grouprace.core.model.Record;
import com.grouprace.core.model.RoutePoint;
import com.grouprace.core.network.model.CreateRecordRequest;
import com.grouprace.core.network.model.NetworkRecord;
import com.grouprace.core.network.model.NetworkRoutePoint;
import com.grouprace.core.network.source.RecordNetworkDataSource;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class TrackingRepositoryImpl implements TrackingRepository {

    private static final String TAG = "TrackingRepository";
    private final RoutePointDao routePointDao;
    private final RecordDao recordDao;
    private final RecordNetworkDataSource networkDataSource;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Inject
    public TrackingRepositoryImpl(RoutePointDao routePointDao, RecordDao recordDao, RecordNetworkDataSource networkDataSource) {
        this.routePointDao = routePointDao;
        this.recordDao = recordDao;
        this.networkDataSource = networkDataSource;
    }

    @Override
    public void savePoint(RoutePoint point) {
        executor.execute(() -> {
            com.grouprace.core.data.model.RoutePoint entity = new com.grouprace.core.data.model.RoutePoint(
                    point.activityId, point.latitude, point.longitude,
                    point.altitude, point.timestamp, point.accuracy
            );
            routePointDao.insert(entity);
        });
    }

    /**
     * Phase 5 & 7: Synchronization.
     * Collects all local points (ID 0), sends them to backend, and then updates 
     * the local records with the real ID returned by the server.
     */
    @Override
    public LiveData<Result<Long>> createRecord(Record record) {
        MutableLiveData<Result<Long>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(new Result.Loading<>());

        executor.execute(() -> {
            try {
                // Get points captured during session (they all have activityId = 0 initially)
                List<com.grouprace.core.data.model.RoutePoint> localPoints = routePointDao.getByActivityId(0);
                List<NetworkRoutePoint> networkPoints = localPoints.stream()
                        .map(p -> new NetworkRoutePoint(p.latitude, p.longitude, p.altitude, p.timestamp, p.accuracy))
                        .collect(Collectors.toList());

                CreateRecordRequest request = new CreateRecordRequest(
                        record.getActivityType(),
                        record.getTitle(),
                        record.getStartTime(),
                        record.getEndTime(),
                        record.getDuration(),
                        record.getDistance(),
                        record.getCalories(),
                        record.getHeartRate(),
                        record.getSpeed(),
                        record.getImageUrl(),
                        networkPoints
                );

                mainHandler.post(() -> {
                    LiveData<Result<NetworkRecord>> networkCall = networkDataSource.createRecord(request);
                    networkCall.observeForever(networkResult -> {
                        if (networkResult instanceof Result.Success) {
                            NetworkRecord nr = ((Result.Success<NetworkRecord>) networkResult).data;
                            executor.execute(() -> {
                                RecordEntity entity = new RecordEntity(
                                        nr.getRecordId(),
                                        nr.getActivityType(),
                                        nr.getTitle(),
                                        nr.getStartTime(),
                                        nr.getEndTime(),
                                        nr.getOwnerId(),
                                        nr.getDuration(),
                                        nr.getDistance(),
                                        nr.getCalories(),
                                        nr.getHeartRate(),
                                        nr.getSpeed(),
                                        nr.getImageUrl()
                                );
                                recordDao.insert(entity);
                                routePointDao.assignUnassignedPointsToActivity(nr.getRecordId());
                                resultLiveData.postValue(new Result.Success<>((long) nr.getRecordId()));
                            });
                        } else if (networkResult instanceof Result.Error) {
                            Result.Error<NetworkRecord> error = (Result.Error<NetworkRecord>) networkResult;
                            resultLiveData.postValue(new Result.Error<>(error.exception, error.message));
                        }
                    });
                });
            } catch (Exception e) {
                Log.e(TAG, "Exception during remote record creation prep", e);
                resultLiveData.postValue(new Result.Error<>(e, e.getMessage()));
            }
        });

        return resultLiveData;
    }

    @Override
    public LiveData<Result<Void>> updateRecord(Record record) {
        MutableLiveData<Result<Void>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(new Result.Loading<>());
 
        java.util.Map<String, Object> updateData = new java.util.HashMap<>();
        updateData.put("title", record.getTitle());
        // Add other fields if needed
 
        mainHandler.post(() -> {
            LiveData<Result<Void>> updateCall = networkDataSource.updateRecord(record.getRecordId(), updateData);
            updateCall.observeForever(new Observer<Result<Void>>() {
                @Override
                public void onChanged(Result<Void> result) {
                    if (result instanceof Result.Success) {
                        updateRecordLocal(record);
                        resultLiveData.postValue(new Result.Success<>(null));
                        updateCall.removeObserver(this);
                    } else if (result instanceof Result.Error) {
                        resultLiveData.postValue(result);
                        updateCall.removeObserver(this);
                    }
                }
            });
        });
 
        return resultLiveData;
    }
 
    @Override
    public void updateRecordLocal(Record record) {
        executor.execute(() -> {
            RecordEntity entity = new RecordEntity(
                    record.getRecordId(),
                    record.getActivityType(),
                    record.getTitle(),
                    record.getStartTime(),
                    record.getEndTime(),
                    record.getOwnerId(),
                    record.getDuration(),
                    record.getDistance(),
                    record.getCalories(),
                    record.getHeartRate(),
                    record.getSpeed(),
                    record.getImageUrl()
            );
            recordDao.update(entity);
        });
    }

    @Override
    public Record getRecordById(long id) {
        try {
            Future<Record> future = executor.submit(() -> {
                RecordEntity entity = recordDao.getById((int) id);
                if (entity == null) return null;
                return entity.asExternalModel();
            });
            return future.get();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get record by id: " + id, e);
            return null;
        }
    }

    @Override
    public List<RoutePoint> getPointsForRecord(long recordId) {
        try {
            Future<List<RoutePoint>> future = executor.submit(() -> {
                List<com.grouprace.core.data.model.RoutePoint> entities = routePointDao.getByActivityId(recordId);
                List<RoutePoint> models = new java.util.ArrayList<>();
                if (entities != null) {
                    for (com.grouprace.core.data.model.RoutePoint rp : entities) {
                        models.add(new RoutePoint(
                                rp.activityId, rp.latitude, rp.longitude,
                                rp.altitude, rp.timestamp, rp.accuracy
                        ));
                    }
                }
                return models;
            });
            return future.get();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get points for record: " + recordId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public void clearUnassignedPoints() {
        executor.execute(() -> {
            routePointDao.deleteUnassignedPoints();
        });
    }
}
