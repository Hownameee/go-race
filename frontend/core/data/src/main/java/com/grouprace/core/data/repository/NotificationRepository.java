package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import com.grouprace.core.model.NotificationModel;

import java.util.List;

public interface NotificationRepository {

    LiveData<List<NotificationModel>> getNotifications();
    void refreshNotifications();
    void registerDeviceToken(int userId, String token);

    void startSocket(int userId);

    void addNotification(NotificationModel notification);

    void clearAll();

    void disconnect();
}