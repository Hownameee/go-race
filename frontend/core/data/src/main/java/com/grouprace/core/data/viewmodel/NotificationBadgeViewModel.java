package com.grouprace.core.data.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.data.repository.NotificationRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NotificationBadgeViewModel extends ViewModel {

    private final LiveData<Integer> unreadCount;

    @Inject
    public NotificationBadgeViewModel(NotificationRepository repository) {
        this.unreadCount = repository.getUnreadCount();

        // Fetch notifications from server immediately so the badge reflects
        // the real unread count even before the user opens NotificationFragment.
        repository.refreshNotifications();
    }

    /**
     * Returns a reactive count of unread notifications.
     * Backed by a Room LiveData query — updates automatically when
     * notifications are inserted or marked as read.
     */
    public LiveData<Integer> getUnreadCount() {
        return unreadCount;
    }
}
