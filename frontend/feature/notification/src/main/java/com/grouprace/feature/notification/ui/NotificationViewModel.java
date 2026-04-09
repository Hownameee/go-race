package com.grouprace.feature.notification.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
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
    private final MutableLiveData<Boolean> refreshTrigger = new MutableLiveData<>();
    private final List<NotificationModel> allNotifications = new ArrayList<>();
    private boolean hasLoaded = false;

    @Inject
    public NotificationViewModel(NotificationRepository repository) {
        this.repository = repository;
        setupNotificationsStream();
    }

    private void setupNotificationsStream() {
        notificationsResult.addSource(refreshTrigger, trigger -> {
            LiveData<Result<List<NotificationModel>>> source = repository.getNotifications();

            notificationsResult.addSource(source, result -> {
                if (result instanceof Result.Loading) {
                    notificationsResult.setValue(new Result.Loading<>());
                } else if (result instanceof Result.Success) {
                    handleSuccess(((Result.Success<List<NotificationModel>>) result).data);
                } else if (result instanceof Result.Error) {
                    Result.Error<?> error = (Result.Error<?>) result;
                    notificationsResult.setValue(new Result.Error<>(error.exception, error.message));
                }
                notificationsResult.removeSource(source);
            });
        });
    }

    private void handleSuccess(List<NotificationModel> newList) {
        allNotifications.clear();
        if (newList != null && !newList.isEmpty()) {
            allNotifications.addAll(newList);
        }
        notificationsResult.setValue(new Result.Success<>(new ArrayList<>(allNotifications)));
    }

    public void refreshNotifications() {
        hasLoaded = true;
        refreshTrigger.setValue(true);
        repository.refreshNotifications();
    }

    public void addNotification(NotificationModel notification) {
        allNotifications.add(0, notification);
        notificationsResult.setValue(new Result.Success<>(new ArrayList<>(allNotifications)));
    }

    public LiveData<Result<List<NotificationModel>>> getNotifications() {
        if (!hasLoaded) {
            refreshNotifications();
        }
        return notificationsResult;
    }
    public void markAsRead(NotificationModel notification) {
        notification.setRead(true);
        notificationsResult.setValue(new Result.Success<>(new ArrayList<>(allNotifications)));
        LiveData<Result<Boolean>> source = repository.markAsRead(notification.getId());

        notificationsResult.addSource(source, result -> {
            if (result instanceof Result.Error) {
                notification.setRead(false);
                notificationsResult.setValue(new Result.Success<>(new ArrayList<>(allNotifications)));
            }
            notificationsResult.removeSource(source);
        });
    }
}