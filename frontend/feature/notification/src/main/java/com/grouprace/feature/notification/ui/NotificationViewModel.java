package com.grouprace.feature.notification.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.NotificationRepository;
import com.grouprace.core.model.NotificationModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NotificationViewModel extends ViewModel {

    private final NotificationRepository repository;
    private final MediatorLiveData<Result<List<NotificationModel>>> notificationsResult = new MediatorLiveData<>();
    private boolean hasLoaded = false;

    @Inject
    public NotificationViewModel(NotificationRepository repository) {
        this.repository = repository;
        setupNotificationsStream();
    }

    private void setupNotificationsStream() {
        LiveData<Result<List<NotificationModel>>> source = repository.getNotifications();

        notificationsResult.addSource(source, notificationsResult::setValue);
    }

    public void refreshNotifications() {
        hasLoaded = true;
        repository.refreshNotifications();
    }

    public void loadMoreNotifications() {
        repository.loadMoreNotifications();
    }

    public void addNotification(NotificationModel notification) {
        repository.addNotification(notification);
    }

    public LiveData<Result<List<NotificationModel>>> getNotifications() {
        if (!hasLoaded) {
            refreshNotifications();
        }
        return notificationsResult;
    }

    public void markAsRead(NotificationModel notification) {
        // Just trigger repository. Room DB will emit updated state automatically.
        markAsRead(notification.getId());
    }

    public void markAsRead(int notificationId) {
        LiveData<Result<Boolean>> source = repository.markAsRead(notificationId);

        notificationsResult.addSource(source, result -> {
            if (result instanceof Result.Error) {
                // If we have the notification object in our list, we could revert its state,
                // but for ID-only call, we just complete.
            }
            if (!(result instanceof Result.Loading)) {
                notificationsResult.removeSource(source);
            }
        });
    }
}