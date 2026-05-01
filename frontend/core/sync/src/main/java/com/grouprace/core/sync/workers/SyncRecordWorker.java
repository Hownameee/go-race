package com.grouprace.core.sync.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.PostDao;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class SyncRecordWorker extends Worker {

    private final RecordDao recordDao;
    private final PostDao postDao;
    private final com.grouprace.core.data.dao.RoutePointDao routePointDao;
    private final com.grouprace.core.network.source.RecordNetworkDataSource networkDataSource;

    @AssistedInject
    public SyncRecordWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            RecordDao recordDao,
            PostDao postDao,
            com.grouprace.core.data.dao.RoutePointDao routePointDao,
            com.grouprace.core.network.source.RecordNetworkDataSource networkDataSource
    ) {
        super(context, workerParams);
        this.recordDao = recordDao;
        this.postDao = postDao;
        this.routePointDao = routePointDao;
        this.networkDataSource = networkDataSource;
    }

    @NonNull
    @Override
    public Result doWork() {
        java.util.List<com.grouprace.core.data.model.RecordEntity> pendingRecords = recordDao.getPendingRecords();
        Log.d("SyncRecordWorker", "Starting sync records work. Found " + pendingRecords.size() + " records to sync.");

        if (pendingRecords.isEmpty()) {
            return Result.success();
        }

        boolean allSuccessful = true;

        for (com.grouprace.core.data.model.RecordEntity record : pendingRecords) {
            try {
                if (record.recordId < 0) {
                    // CASE 1: Record Creation (Negative ID)
                    // 1. Fetch associated points
                    java.util.List<com.grouprace.core.data.model.RoutePoint> localPoints = routePointDao.getByActivityId(record.recordId);
                    java.util.List<com.grouprace.core.network.model.NetworkRoutePoint> networkPoints = localPoints.stream()
                            .map(p -> new com.grouprace.core.network.model.NetworkRoutePoint(p.latitude, p.longitude, p.altitude, p.timestamp, p.accuracy))
                            .collect(java.util.stream.Collectors.toList());

                    // 2. Prepare request
                    com.grouprace.core.network.model.CreateRecordRequest request = new com.grouprace.core.network.model.CreateRecordRequest(
                            record.activityType, record.title, record.startTime, record.endTime,
                            record.duration, record.distance, record.calories, record.heartRate,
                            record.speed, record.imageUrl, networkPoints
                    );

                    // 3. Upload to server
                    com.grouprace.core.common.result.Result<com.grouprace.core.network.model.NetworkRecord> result = 
                            networkDataSource.createRecordSync(request);

                    if (result instanceof com.grouprace.core.common.result.Result.Success) {
                        com.grouprace.core.network.model.NetworkRecord nr = 
                                ((com.grouprace.core.common.result.Result.Success<com.grouprace.core.network.model.NetworkRecord>) result).data;
                        
                        int oldId = record.recordId;
                        int newId = nr.getRecordId();
                        android.util.Log.d("SyncRecordWorker", "Successfully created record: " + oldId + " -> " + newId);
                        
                        postDao.updatePendingPostRecordIds(oldId, newId);
                        routePointDao.updateActivityId(oldId, newId);

                        com.grouprace.core.data.model.RecordEntity syncedEntity = new com.grouprace.core.data.model.RecordEntity(
                                newId, nr.getActivityType(), nr.getTitle(), nr.getStartTime(),
                                nr.getEndTime(), nr.getOwnerId(), nr.getDuration(),
                                nr.getDistance(), nr.getCalories(), nr.getHeartRate(),
                                nr.getSpeed(), nr.getImageUrl(), false
                        );
                        
                        recordDao.deleteById(oldId);
                        recordDao.insert(syncedEntity);
                    } else {
                        if (result instanceof com.grouprace.core.common.result.Result.Error) {
                            Log.e("SyncRecordWorker", "Server Error (Create): " + ((com.grouprace.core.common.result.Result.Error<?>) result).message);
                        }
                        allSuccessful = false;
                    }
                } else {
                    // CASE 2: Record Update (Positive ID + pendingSync)
                    android.util.Log.d("SyncRecordWorker", "Updating existing record: " + record.recordId);
                    
                    java.util.Map<String, Object> updateData = new java.util.HashMap<>();
                    updateData.put("title", record.title);
                    // Add other fields if necessary
                    
                    com.grouprace.core.common.result.Result<Void> result = 
                            networkDataSource.updateRecordSync(record.recordId, updateData);
                            
                    if (result instanceof com.grouprace.core.common.result.Result.Success) {
                        android.util.Log.d("SyncRecordWorker", "Successfully updated record title for " + record.recordId);
                        record.pendingSync = false;
                        recordDao.update(record);
                    } else {
                        if (result instanceof com.grouprace.core.common.result.Result.Error) {
                            Log.e("SyncRecordWorker", "Server Error (Update): " + ((com.grouprace.core.common.result.Result.Error<?>) result).message);
                        }
                        allSuccessful = false;
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("SyncRecordWorker", "Error syncing record " + record.recordId, e);
                allSuccessful = false;
            }
        }
        return allSuccessful ? Result.success() : Result.retry();
    }
}
