package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

    @Override
    public LiveData<Result<Long>> createRecord(Record record) {
        MutableLiveData<Result<Long>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(new Result.Loading<>());

        executor.execute(() -> {
            try {
                // Save locally first with a negative temp ID — user gets instant feedback
                int tempId = -(int)(System.currentTimeMillis() % 100_000) - 1;
                RecordEntity localEntity = new RecordEntity(
                        tempId, record.getActivityType(), record.getTitle(),
                        record.getStartTime(), record.getEndTime(), record.getOwnerId(),
                        record.getDuration(), record.getDistance(), record.getCalories(),
                        record.getHeartRate(), record.getSpeed(), record.getImageUrl(), true);
                recordDao.insert(localEntity);
                routePointDao.assignUnassignedPointsToActivity(tempId);

                resultLiveData.postValue(new Result.Success<>((long) tempId));

                // Background sync — swap temp ID with real server ID on success
                syncNewRecord(tempId);
            } catch (Exception e) {
                Log.e(TAG, "Failed to save record locally", e);
                resultLiveData.postValue(new Result.Error<>(e, e.getMessage()));
            }
        });

        return resultLiveData;
    }

    private void syncNewRecord(int tempId) {
        executor.execute(() -> {
            try {
                RecordEntity local = recordDao.getById(tempId);
                if (local == null) return;

                List<com.grouprace.core.data.model.RoutePoint> localPoints = routePointDao.getByActivityId(tempId);
                List<NetworkRoutePoint> networkPoints = localPoints.stream()
                        .map(p -> new NetworkRoutePoint(p.latitude, p.longitude, p.altitude, p.timestamp, p.accuracy))
                        .collect(Collectors.toList());

                CreateRecordRequest request = new CreateRecordRequest(
                        local.activityType, local.title, local.startTime, local.endTime,
                        local.duration, local.distance, local.calories,
                        local.heartRate, local.speed, local.imageUrl, networkPoints);

                mainHandler.post(() -> {
                    LiveData<Result<NetworkRecord>> call = networkDataSource.createRecord(request);
                    call.observeForever(new androidx.lifecycle.Observer<Result<NetworkRecord>>() {
                        @Override
                        public void onChanged(Result<NetworkRecord> result) {
                            if (result instanceof Result.Loading) return;
                            call.removeObserver(this);
                            if (result instanceof Result.Success) {
                                NetworkRecord nr = ((Result.Success<NetworkRecord>) result).data;
                                executor.execute(() -> {
                                    // Read latest local state — user may have edited title during sync
                                    RecordEntity latest = recordDao.getById(tempId);
                                    String title = latest != null ? latest.title : nr.getTitle();
                                    recordDao.deleteById(tempId);
                                    RecordEntity real = new RecordEntity(
                                            nr.getRecordId(), nr.getActivityType(), title,
                                            nr.getStartTime(), nr.getEndTime(), nr.getOwnerId(),
                                            nr.getDuration(), nr.getDistance(), nr.getCalories(),
                                            nr.getHeartRate(), nr.getSpeed(), nr.getImageUrl(), false);
                                    recordDao.insert(real);
                                    routePointDao.reassignPoints(tempId, nr.getRecordId());
                                });
                            } else if (result instanceof Result.Error) {
                                Log.w(TAG, "Background sync failed — record stays local: tempId=" + tempId);
                            }
                        }
                    });
                });
            } catch (Exception e) {
                Log.e(TAG, "Background sync exception: tempId=" + tempId, e);
            }
        });
    }

    @Override
    public LiveData<Result<Void>> updateRecord(Record record) {
        MutableLiveData<Result<Void>> resultLiveData = new MutableLiveData<>();

        // Persist locally first — user never waits for network
        updateRecordLocal(record);
        resultLiveData.setValue(new Result.Success<>(null));

        // Temp-ID records haven't reached the server yet; syncNewRecord will pick up the latest title
        if (record.getRecordId() < 0) return resultLiveData;

        java.util.Map<String, Object> updateData = new java.util.HashMap<>();
        updateData.put("title", record.getTitle());

        mainHandler.post(() -> {
            LiveData<Result<Void>> call = networkDataSource.updateRecord(record.getRecordId(), updateData);
            call.observeForever(new androidx.lifecycle.Observer<Result<Void>>() {
                @Override
                public void onChanged(Result<Void> result) {
                    if (result instanceof Result.Loading) return;
                    call.removeObserver(this);
                    if (result instanceof Result.Error) {
                        executor.execute(() -> recordDao.setPendingSync(record.getRecordId(), true));
                    }
                }
            });
        });

        return resultLiveData;
    }

    @Override
    public void updateRecordLocal(Record record) {
        executor.execute(() -> {
            RecordEntity existing = recordDao.getById(record.getRecordId());
            boolean pendingSync = existing != null && existing.pendingSync;
            RecordEntity entity = new RecordEntity(
                    record.getRecordId(), record.getActivityType(), record.getTitle(),
                    record.getStartTime(), record.getEndTime(), record.getOwnerId(),
                    record.getDuration(), record.getDistance(), record.getCalories(),
                    record.getHeartRate(), record.getSpeed(), record.getImageUrl(), pendingSync);
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
