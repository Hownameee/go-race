package com.grouprace.feature.notification.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.model.NotificationModel;
import com.grouprace.core.notification.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class NotificationViewModel extends ViewModel {

    // Danh sách lưu trữ notification
    private final List<NotificationModel> notificationList = new ArrayList<>();

    // LiveData mà UI sẽ observe
    private final MutableLiveData<List<NotificationModel>> notifications = new MutableLiveData<>();

    public LiveData<List<NotificationModel>> getNotifications() {
        return notifications;
    }

    /**
     * Bắt đầu kết nối socket và nhận realtime notifications
     */
    public void startSocket(int userId) {
        // Kết nối socket
        NotificationHelper.getInstance().connect(userId);

        // Đăng ký listener để nhận notification từ server
        NotificationHelper.getInstance().setNotificationListener(notification -> {
            // notification bây giờ là NotificationModel đầy đủ
            addNotification(notification);
        });
    }

    /**
     * Thêm notification vào list và update LiveData
     */
    public void addNotification(NotificationModel notification) {
        notificationList.add(notification);

        // Cập nhật LiveData
        notifications.postValue(new ArrayList<>(notificationList));
    }

    /**
     * Tạo notification thủ công (test) từ title & message
     */
    public void addNotificationManual(String title, String message) {
        // Tạo NotificationModel dummy với giá trị mặc định
        NotificationModel notification = new NotificationModel(
                -1,            // id chưa có
                -1,            // userId chưa có
                "system",      // type mặc định
                null,          // actorId
                null,          // activityId
                title,
                message,
                ""             // createdAt trống
        );

        addNotification(notification);
    }

    /**
     * Xóa hết notification
     */
    public void clearAll() {
        notificationList.clear();
        notifications.setValue(new ArrayList<>(notificationList));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Ngắt socket khi ViewModel bị hủy
        NotificationHelper.getInstance().disconnect();
    }
}