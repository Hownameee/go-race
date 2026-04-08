package com.grouprace.core.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.grouprace.core.model.NotificationModel;

public class NotificationHelper {

    private static final String CHANNEL_ID = "grouprace_channel";
    private static NotificationHelper instance;
    private NotificationListener listener;

    // Singleton
    private NotificationHelper() {}

    public static NotificationHelper getInstance() {
        if (instance == null) {
            instance = new NotificationHelper();
        }
        return instance;
    }

    // Set listener để ViewModel/Fragment nhận notification realtime
    public void setNotificationListener(NotificationListener listener) {
        this.listener = listener;
    }

    /**
     * Hiển thị notification Android cục bộ
     */
    public static void showNotification(Context context, int id, String title, String message, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "GroupRace Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                manager.createNotificationChannel(channel);
            }
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(com.grouprace.core.system.R.drawable.ic_app)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        manager.notify(id, builder.build());
    }

    // Interface để callback tới ViewModel/Fragment
    public interface NotificationListener {
        void onNewNotification(NotificationModel notification);
    }
}