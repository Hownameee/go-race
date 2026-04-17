package com.grouprace.core.data.repository;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.NotificationModel;
import com.grouprace.core.network.model.notification.NetworkNotification;
import com.grouprace.core.network.source.NotificationNetworkDataSource;
import com.grouprace.core.data.dao.NotificationDao;
import com.grouprace.core.data.model.NotificationEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationNetworkDataSource networkDataSource;
    private final NotificationDao notificationDao;
    private final MediatorLiveData<Result<List<NotificationModel>>> notificationsLiveData = new MediatorLiveData<>();
    
    private boolean isFetching = false;
    private List<NotificationModel> cachedModels = new ArrayList<>();

    @Inject
    public NotificationRepositoryImpl(NotificationNetworkDataSource networkDataSource, NotificationDao notificationDao) {
        this.networkDataSource = networkDataSource;
        this.notificationDao = notificationDao;
        
        notificationsLiveData.addSource(notificationDao.getNotifications(), entities -> {
            if (entities != null) {
                cachedModels = entities.stream()
                        .map(this::toModel)
                        .collect(Collectors.toList());
                pushToLiveData();
            }
        });
    }

    private void pushToLiveData() {
        if (isFetching && cachedModels.isEmpty()) {
            notificationsLiveData.setValue(new Result.Loading<>());
        } else {
            notificationsLiveData.setValue(new Result.Success<>(new ArrayList<>(cachedModels)));
        }
    }

    @Override
    public LiveData<Result<List<NotificationModel>>> getNotifications() {
        return notificationsLiveData;
    }

    @Override
    public void refreshNotifications() {
        isFetching = true;
        pushToLiveData();

        LiveData<Result<List<NetworkNotification>>> source = networkDataSource.getNotifications();

        notificationsLiveData.addSource(source, result -> {
            if (result instanceof Result.Success) {
                isFetching = false;
                List<NetworkNotification> networkList = ((Result.Success<List<NetworkNotification>>) result).data;
                new Thread(() -> {
                    List<NotificationEntity> entities = networkList.stream()
                            .map(this::networkToEntity)
                            .collect(Collectors.toList());
                    notificationDao.insertAll(entities);
                    
                    if (entities.isEmpty()) {
                        notificationsLiveData.postValue(new Result.Success<>(new ArrayList<>()));
                    }
                }).start();
            } else if (result instanceof Result.Error) {
                isFetching = false;
                Result.Error<List<NetworkNotification>> error = (Result.Error<List<NetworkNotification>>) result;
                notificationsLiveData.setValue(new Result.Error<>(error.exception, error.message));
            } else if (result instanceof Result.Loading) {
                // Still Loading
            }
            
            if (!(result instanceof Result.Loading)) {
                notificationsLiveData.removeSource(source);
            }
        });
    }

    @Override
    public void addNotification(NotificationModel notification) {
        new Thread(() -> {
            notificationDao.insertAll(Collections.singletonList(toEntity(notification)));
        }).start();
    }

    @Override
    public void clearAll() {
        new Thread(() -> {
            notificationDao.clearAll();
        }).start();
    }
    
    @Override
    public LiveData<Result<Boolean>> markAsRead(int notificationId) {
        new Thread(() -> {
            notificationDao.markAsRead(notificationId);
        }).start();
        return networkDataSource.markAsRead(notificationId);
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
                entity.read
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
        entity.read = model.isRead() != null ? model.isRead() : false;
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
        return entity;
    }
}