package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.grouprace.core.model.NotificationModel;
import com.grouprace.core.network.model.NetworkNotification;
import com.grouprace.core.network.source.NotificationNetworkDataSource;
import com.grouprace.core.notification.NotificationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationNetworkDataSource networkDataSource;
    private final MediatorLiveData<List<NotificationModel>> notificationsLiveData = new MediatorLiveData<>();
    private final List<NotificationModel> cache = new ArrayList<>();
    private boolean hasLoaded = false;

    @Inject
    public NotificationRepositoryImpl(NotificationNetworkDataSource networkDataSource) {
        this.networkDataSource = networkDataSource;
    }

    @Override
    public LiveData<List<NotificationModel>> getNotifications() {
        if (!hasLoaded) {
            refreshNotifications();
            hasLoaded = true;
        }
        return notificationsLiveData;
    }

    @Override
    public void refreshNotifications() {
        LiveData<List<NetworkNotification>> fetchSource = networkDataSource.getNotifications();
        notificationsLiveData.addSource(fetchSource, networkNotifications -> {
            List<NotificationModel> mapped = mapNetworkNotifications(networkNotifications);
            cache.clear();
            cache.addAll(mapped);
            notificationsLiveData.setValue(new ArrayList<>(cache));
            // One-shot source from Retrofit callback; remove to avoid accumulating sources.
            notificationsLiveData.removeSource(fetchSource);
        });
    }

    @Override
    public void registerDeviceToken(int userId, String token) {
        networkDataSource.registerDeviceToken(userId, token);
    }

    @Override
    public void startSocket(int userId) {
        NotificationHelper.getInstance().connect(userId);
        NotificationHelper.getInstance().setNotificationListener(notification -> {
            cache.add(0, notification);
            notificationsLiveData.postValue(new ArrayList<>(cache));
        });
    }

    @Override
    public void addNotification(NotificationModel notification) {
        cache.add(0, notification);
        notificationsLiveData.postValue(new ArrayList<>(cache));
    }

    @Override
    public void clearAll() {
        cache.clear();
        notificationsLiveData.postValue(new ArrayList<>());
    }

    @Override
    public void disconnect() {
        NotificationHelper.getInstance().disconnect();
    }

    private List<NotificationModel> mapNetworkNotifications(List<NetworkNotification> networkNotifications) {
        if (networkNotifications == null) {
            return new ArrayList<>();
        }

        return networkNotifications.stream()
                .map(networkNotification -> new NotificationModel(
                        networkNotification.getId(),
                        networkNotification.getUserId(),
                        networkNotification.getType(),
                        networkNotification.getActorId(),
                        networkNotification.getActivityId(),
                        networkNotification.getTitle(),
                        networkNotification.getMessage(),
                        networkNotification.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}