package com.grouprace.feature.notification.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.data.repository.NotificationRepository;
import com.grouprace.core.model.NotificationModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NotificationViewModel extends ViewModel {

    private final NotificationRepository repository;
    private final LiveData<List<NotificationModel>> notifications;

    @Inject
    public NotificationViewModel(NotificationRepository repository) {
        this.repository = repository;
        this.notifications = repository.getNotifications();
    }

    public LiveData<List<NotificationModel>> getNotifications() {
        return notifications;
    }

    public void startSocket(int userId) {
        repository.startSocket(userId);
    }

    public void addNotification(NotificationModel notification) {
        repository.addNotification(notification);
    }

    public void disconnect() {
        repository.disconnect();
    }

    public void refreshNotifications() {
        repository.refreshNotifications();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.disconnect();
    }
}