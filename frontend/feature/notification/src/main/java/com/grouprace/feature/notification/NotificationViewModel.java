package com.grouprace.feature.notification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.model.NotificationModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationViewModel extends ViewModel {

    // Internal list that holds the actual data
    private final List<NotificationModel> notificationList = new ArrayList<>();

    // LiveData that the Activity/Fragment will observe
    private final MutableLiveData<List<NotificationModel>> notifications = new MutableLiveData<>();

    // Getter for the LiveData (Publicly immutable)
    public LiveData<List<NotificationModel>> getNotifications() {
        return notifications;
    }

    // Method to add a notification and update the observer
    public void addNotification(String title, String message) {
        notificationList.add(new NotificationModel(title, message));

        // Post the updated list to the LiveData
        // Use .setValue() if on main thread, .postValue() if from background
        notifications.setValue(new ArrayList<>(notificationList));
    }

    // Clear notifications
    public void clearAll() {
        notificationList.clear();
        notifications.setValue(new ArrayList<>(notificationList));
    }
}