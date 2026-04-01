package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.NotificationModel;

import java.util.List;

public interface NotificationRepository {

    LiveData<Result<List<NotificationModel>>> getNotifications();
    void refreshNotifications();
    void registerDeviceToken(int userId, String token);

    void addNotification(NotificationModel notification);

    LiveData<Result<Boolean>> markAsRead(int notificationId);

    void clearAll();
}