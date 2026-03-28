package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.NotificationModel;
import com.grouprace.core.network.model.NetworkNotification;
import com.grouprace.core.network.source.NotificationNetworkDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationNetworkDataSource networkDataSource;
    private final MediatorLiveData<Result<List<NotificationModel>>> notificationsLiveData = new MediatorLiveData<>();
    private final List<NotificationModel> cache = new ArrayList<>();

    @Inject
    public NotificationRepositoryImpl(NotificationNetworkDataSource networkDataSource) {
        this.networkDataSource = networkDataSource;
    }

    @Override
    public LiveData<Result<List<NotificationModel>>> getNotifications() {
        return notificationsLiveData;
    }

    @Override
    public void refreshNotifications() {
        LiveData<Result<List<NetworkNotification>>> source = networkDataSource.getNotifications();

        notificationsLiveData.addSource(source, result -> {
            if (result instanceof Result.Success) {
                List<NetworkNotification> networkList = ((Result.Success<List<NetworkNotification>>) result).data;
                List<NotificationModel> mapped = mapNetworkNotifications(networkList);
                cache.clear();
                cache.addAll(mapped);
                notificationsLiveData.setValue(new Result.Success<>(new ArrayList<>(cache)));
            } else if (result instanceof Result.Error) {
                Result.Error<List<NetworkNotification>> error = (Result.Error<List<NetworkNotification>>) result;
                notificationsLiveData.setValue(new Result.Error<>(error.exception, error.message));
            } else {
                notificationsLiveData.setValue(new Result.Loading<>());
            }
            notificationsLiveData.removeSource(source); // tránh giữ nhiều source
        });
    }

    @Override
    public void registerDeviceToken(int userId, String token) {
        networkDataSource.registerDeviceToken(userId, token);
    }

    @Override
    public void addNotification(NotificationModel notification) {
        cache.add(0, notification);
        notificationsLiveData.postValue(new Result.Success<>(new ArrayList<>(cache)));
    }

    @Override
    public void clearAll() {
        cache.clear();
        notificationsLiveData.postValue(new Result.Success<>(new ArrayList<>()));
    }

    private List<NotificationModel> mapNetworkNotifications(List<NetworkNotification> networkNotifications) {
        if (networkNotifications == null) return new ArrayList<>();

        return networkNotifications.stream()
                .map(n -> new NotificationModel(
                        n.getId(),
                        n.getUserId(),
                        n.getType(),
                        n.getActorId(),
                        n.getActivityId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.getCreatedAt(),
                        n.isRead()
                ))
                .collect(Collectors.toList());
    }
}