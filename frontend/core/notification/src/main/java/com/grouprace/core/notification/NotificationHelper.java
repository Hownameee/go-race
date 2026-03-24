package com.grouprace.core.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.grouprace.core.model.NotificationModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class NotificationHelper {

    private static final String CHANNEL_ID = "grouprace_channel";
    private static NotificationHelper instance;
    private Socket mSocket;
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
     * Kết nối socket với server
     */
    public void connect(int userId) {
        if (mSocket != null && mSocket.connected()) return;

        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            options.transports = new String[]{"websocket"};

            mSocket = IO.socket("http://10.0.2.2:5000", options);
            mSocket.connect();

            // Khi kết nối thành công
            mSocket.on(Socket.EVENT_CONNECT, args -> {
                Log.d("SocketIO", "✅ Connected to server, userId=" + userId);

                // Tham gia room user
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("userId", userId);
                    mSocket.emit("join", obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            // Khi kết nối lỗi
            mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                Log.e("SocketIO", "❌ Connection error: " + args[0]);
            });

            // Khi bị ngắt kết nối
            mSocket.on(Socket.EVENT_DISCONNECT, args -> {
                Log.d("SocketIO", "🔌 Disconnected from server");
            });

            // Lắng nghe notification
            mSocket.on("notification", onNotification);


        } catch (URISyntaxException e) {
            Log.e("SocketIO", "URISyntaxException: " + e.getMessage());
        }
    }
    /**
     * Ngắt kết nối socket
     */
    public void disconnect() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off("notification", onNotification);
            Log.d("SocketIO", "Socket disconnected");
        }
    }

    // Listener nhận dữ liệu từ server
    private final Emitter.Listener onNotification = args -> {
        if (args.length > 0 && listener != null) {
            try {
                JSONObject obj = (JSONObject) args[0];

                // Parse tất cả field từ server để tạo NotificationModel đầy đủ
                int id = obj.optInt("id", -1);
                int userId = obj.optInt("user_id", -1);
                String type = obj.optString("type", "system");
                Integer actorId = obj.has("actor_id") && !obj.isNull("actor_id") ? obj.getInt("actor_id") : null;
                Integer activityId = obj.has("activity_id") && !obj.isNull("activity_id") ? obj.getInt("activity_id") : null;
                String title = obj.optString("title", "");
                String message = obj.optString("message", "");
                String createdAt = obj.optString("created_at", "");

                NotificationModel notification = new NotificationModel(
                        id, userId, type, actorId, activityId, title, message, createdAt
                );

                // Callback tới listener (ViewModel/Fragment)
                listener.onNewNotification(notification);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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
                .setSmallIcon(android.R.drawable.ic_dialog_info)
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