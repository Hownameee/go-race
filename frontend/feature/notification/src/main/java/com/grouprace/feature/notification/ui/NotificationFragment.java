package com.grouprace.feature.notification.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.model.NotificationModel;
import com.grouprace.core.notification.NotificationHelper;
import com.grouprace.feature.notification.R;

import java.util.List;

public class NotificationFragment extends Fragment {

    private NotificationViewModel viewModel;
    private int currentUserId;

    private EditText etTitle;
    private EditText etMsg;
    private Button btnSetAlarm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", 1); // -1 nếu chưa login
        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            // TODO: redirect đến login nếu cần
        }

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        // Kết nối Socket.io realtime thông qua ViewModel
        viewModel.startSocket(currentUserId);

        // Observe LiveData notification để tự động hiển thị notification realtime
        viewModel.getNotifications().observe(getViewLifecycleOwner(), this::updateNotifications);

        // Initialize views
        etTitle = view.findViewById(R.id.edtTitle);
        etMsg = view.findViewById(R.id.edtMessage);
        btnSetAlarm = view.findViewById(R.id.btnSetAlarm);

        // Nút tạo notification thủ công
        btnSetAlarm.setOnClickListener(v -> createManualNotification());

        return view;
    }

    /**
     * Hiển thị notification khi LiveData update
     */
    private void updateNotifications(List<NotificationModel> notificationList) {
        if (notificationList.isEmpty()) return;

        // Lấy notification mới nhất
        NotificationModel latest = notificationList.get(notificationList.size() - 1);

        Intent intent = requireContext().getPackageManager()
                .getLaunchIntentForPackage(requireContext().getPackageName());

        NotificationHelper.showNotification(
                requireContext(),
                (int) System.currentTimeMillis(),
                latest.getTitle(),
                latest.getMessage(),
                intent
        );

        Toast.makeText(getContext(), "Nhận notification mới!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Tạo notification thủ công để test
     */
    private void createManualNotification() {
        String titleStr = etTitle.getText().toString().trim();
        String msgStr = etMsg.getText().toString().trim();

        if (titleStr.isEmpty() || msgStr.isEmpty()) {
            Toast.makeText(getContext(), "Nhập đầy đủ title và message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo NotificationModel thủ công với các trường mặc định/placeholder
        NotificationModel manualNotification = new NotificationModel(
                1,           // id placeholder
                currentUserId, // userId hiện tại
                "system",     // type mặc định
                null,         // actorId
                null,         // activityId
                titleStr,
                msgStr,
                ""            // createdAt trống
        );

        // Thêm vào ViewModel để trigger LiveData
        viewModel.addNotification(manualNotification);

        Log.d("NotificationFragment", "Created manual notification: " + titleStr);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Ngắt kết nối socket khi fragment bị destroy
        NotificationHelper.getInstance().disconnect();
    }
}