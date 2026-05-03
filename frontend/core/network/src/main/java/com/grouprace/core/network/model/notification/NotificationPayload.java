package com.grouprace.core.network.model.notification;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationPayload {
    @SerializedName("notifications")
    private List<NetworkNotification> notifications;

    @SerializedName("nextCursor")
    private Integer nextCursor;

    public List<NetworkNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NetworkNotification> notifications) {
        this.notifications = notifications;
    }

    public Integer getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(Integer nextCursor) {
        this.nextCursor = nextCursor;
    }
}
