package com.grouprace.gorace.fcm;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grouprace.core.data.TokenManager;
import com.grouprace.core.data.repository.NotificationRepository;
import com.grouprace.core.model.NotificationModel;
import com.grouprace.core.notification.NotificationHelper;
import com.grouprace.feature.login.ui.LoginViewModel;
import com.grouprace.gorace.MainActivity;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GoRaceFirebaseMessagingService extends FirebaseMessagingService {

    @Inject
    NotificationRepository notificationRepository;

    private static final String CHANNEL_ID = "grouprace_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        Map<String, String> data = message.getData();

        String title = data != null ? data.getOrDefault("title", "GoRace") : "GoRace";
        String body  = data != null ? data.getOrDefault("message", "") : "";

        Log.d("HANDLE INTENT", "data " + data);
        
        // Delegate data parsing and DB caching to the repository
        if (data != null && notificationRepository != null) {
            notificationRepository.handleFcmMessage(data);
        }

        showNotification(title, body, data);
    }



    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        TokenManager.saveToken(getApplicationContext(), token);
    }

    private void showNotification(String title, String message, Map<String, String> data) {
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (manager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "GoRace Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                manager.createNotificationChannel(channel);
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        // FLAG_ACTIVITY_NEW_TASK is required for starting activity from service context.
        // CLEAR_TOP + SINGLE_TOP handles the back stack requirement: 
        // if app is open, it goes back to previous screen; if closed, it ends the task on back.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
            // Tag this intent as coming from a notification to trigger special handling in MainActivity
            intent.putExtra("from_notification", true);
            
            if (data.containsKey("id")) {
                try {
                    intent.putExtra("id", Integer.parseInt(data.get("id")));
                } catch (NumberFormatException ignored) {}
            }
        }

        int notificationId = (int) System.currentTimeMillis();

        NotificationHelper.showNotification(
                this,
                notificationId,
                title,
                message,
                intent
        );
    }
}
