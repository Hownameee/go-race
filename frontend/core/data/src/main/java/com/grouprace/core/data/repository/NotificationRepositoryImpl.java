package com.grouprace.core.data.repository;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.NotificationModel;
import com.grouprace.core.network.model.notification.NetworkNotification;
import com.grouprace.core.network.model.notification.NotificationPayload;
import com.grouprace.core.network.source.NotificationNetworkDataSource;
import com.grouprace.core.network.utils.ApiResponse;
import com.grouprace.core.data.dao.NotificationDao;
import com.grouprace.core.data.model.NotificationEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import retrofit2.Response;

import javax.inject.Inject;

public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationNetworkDataSource networkDataSource;
    private final NotificationDao notificationDao;
    private final MediatorLiveData<Result<List<NotificationModel>>> notificationsLiveData = new MediatorLiveData<>();

    private final Executor dbExecutor = Executors.newSingleThreadExecutor();
  
    private final AtomicBoolean isFetching = new AtomicBoolean(false);
    
    private Integer nextCursor = null;
    private boolean hasMore = true;
    private static final int PAGE_LIMIT = 20;

    @Inject
    public NotificationRepositoryImpl(NotificationNetworkDataSource networkDataSource, NotificationDao notificationDao) {
        this.networkDataSource = networkDataSource;
        this.notificationDao = notificationDao;
        
        notificationsLiveData.addSource(notificationDao.getNotifications(), entities -> {
            if (entities != null) {
                List<NotificationModel> models = entities.stream()
                        .map(this::toModel)
                        .collect(Collectors.toList());
                
                if (isFetching.get() && models.isEmpty()) {
                    notificationsLiveData.setValue(new Result.Loading<>());
                } else {
                    notificationsLiveData.setValue(new Result.Success<>(models));
                }
            }
        });
    }

    @Override
    public LiveData<Result<List<NotificationModel>>> getNotifications() {
        return notificationsLiveData;
    }

    @Override
    public void refreshNotifications() {
        if (!isFetching.compareAndSet(false, true)) return;

        notificationsLiveData.postValue(new Result.Loading<>());

        dbExecutor.execute(() -> {
            try {
                Response<ApiResponse<NotificationPayload>> response =
                        networkDataSource.apiService.getNotifications(null, PAGE_LIMIT).execute();

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<NotificationPayload> body = response.body();
                    if (body.isSuccess() && body.getData() != null) {
                        List<NetworkNotification> networkList = body.getData().getNotifications();
                        nextCursor = body.getData().getNextCursor();
                        hasMore = nextCursor != null;

                        List<NotificationEntity> entities = networkList.stream()
                                .map(this::networkToEntity)
                                .collect(Collectors.toList());
                        
                        notificationDao.clearAll();
                        notificationDao.upsertAll(entities);

                        if (entities.isEmpty()) {
                            notificationsLiveData.postValue(new Result.Success<>(new ArrayList<>()));
                        }
                    } else {
                        notificationsLiveData.postValue(
                                new Result.Error<>(null, body.getMessage()));
                    }
                } else {
                    notificationsLiveData.postValue(
                            new Result.Error<>(null, "HTTP " + response.code()));
                }
            } catch (IOException e) {
                notificationsLiveData.postValue(new Result.Error<>(e, e.getMessage()));
            } finally {
                isFetching.set(false);
            }
        });
    }

    @Override
    public void loadMoreNotifications() {
        if (!hasMore || !isFetching.compareAndSet(false, true)) return;

        dbExecutor.execute(() -> {
            try {
                Response<ApiResponse<NotificationPayload>> response =
                        networkDataSource.apiService.getNotifications(nextCursor, PAGE_LIMIT).execute();

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<NotificationPayload> body = response.body();
                    if (body.isSuccess() && body.getData() != null) {
                        List<NetworkNotification> networkList = body.getData().getNotifications();
                        nextCursor = body.getData().getNextCursor();
                        hasMore = nextCursor != null;

                        List<NotificationEntity> entities = networkList.stream()
                                .map(this::networkToEntity)
                                .collect(Collectors.toList());
                        notificationDao.upsertAll(entities);
                    }
                }
            } catch (IOException e) {
                // Ignore load more errors for now or handle them silently
            } finally {
                isFetching.set(false);
            }
        });
    }


    @Override
    public void addNotification(NotificationModel notification) {
        dbExecutor.execute(() -> {
            notificationDao.upsertAll(Collections.singletonList(toEntity(notification)));
        });
    }

    @Override
    public void clearAll() {
        dbExecutor.execute(notificationDao::clearAll);
    }
    
    @Override
    public LiveData<Result<Boolean>> markAsRead(int notificationId) {
        dbExecutor.execute(() -> notificationDao.markAsRead(notificationId));
        return networkDataSource.markAsRead(notificationId);
    }
    
    @Override
    public void handleFcmMessage(java.util.Map<String, String> data) {
        if (data == null) return;
        
        dbExecutor.execute(() -> {
            try {
                String idStr = data.get("id");
                if (idStr == null || idStr.isEmpty()) {
                    // Invalid FCM or missing ID, trigger full sync to ensure consistency
                    refreshNotifications();
                    return;
                }
                
                int id = Integer.parseInt(idStr);
                int userId = Integer.parseInt(data.getOrDefault("user_id", "0"));
                
                String actorIdStr = data.get("actor_id");
                Integer actorId = (actorIdStr != null && !actorIdStr.isEmpty()) ? Integer.parseInt(actorIdStr) : null;
                
                String activityIdStr = data.get("activity_id");
                Integer activityId = (activityIdStr != null && !activityIdStr.isEmpty()) ? Integer.parseInt(activityIdStr) : null;
                
                String type = data.getOrDefault("type", "SYSTEM");
                String title = data.getOrDefault("title", "");
                String message = data.getOrDefault("message", "");
                String createdAt = data.getOrDefault("createdAt", String.valueOf(System.currentTimeMillis()));
                String avtUrl = data.get("actor_avatar_url");
                
                NotificationModel model = new NotificationModel(
                        id, userId, type, actorId, activityId, title, message, createdAt, false, avtUrl
                );
                
                notificationDao.upsertAll(Collections.singletonList(toEntity(model)));
            } catch (Exception e) {
                // If parsing fails, don't crash. Trigger a network sync to heal the DB.
                refreshNotifications();
            }
        });
    }

    @Override
    public LiveData<Integer> getUnreadCount() {
        return notificationDao.getUnreadCount();
    }
    
    private NotificationModel toModel(NotificationEntity entity) {
        return new NotificationModel(
                entity.id,
                entity.userId,
                entity.type,
                entity.actorId,
                entity.activityId,
                entity.title,
                entity.message,
                entity.createdAt,
                entity.read,
                entity.avtUrl
        );
    }

    private NotificationEntity toEntity(NotificationModel model) {
        NotificationEntity entity = new NotificationEntity();
        entity.id = model.getId();
        entity.userId = model.getUserId();
        entity.type = model.getType();
        entity.actorId = model.getActorId();
        entity.activityId = model.getActivityId();
        entity.title = model.getTitle();
        entity.message = model.getMessage();
        entity.createdAt = model.getCreatedAt();
        entity.read = model.isRead();
        entity.avtUrl = model.getAvtUrl();
        return entity;
    }
    
    private NotificationEntity networkToEntity(NetworkNotification n) {
        NotificationEntity entity = new NotificationEntity();
        entity.id = n.getId();
        entity.userId = n.getUserId();
        entity.type = n.getType();
        entity.actorId = n.getActorId();
        entity.activityId = n.getActivityId();
        entity.title = n.getTitle();
        entity.message = n.getMessage();
        entity.createdAt = n.getCreatedAt();
        entity.read = n.isRead();
        entity.avtUrl = n.getAvtUrl();
        return entity;
    }
}