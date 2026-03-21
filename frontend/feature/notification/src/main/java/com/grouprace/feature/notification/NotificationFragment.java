package com.grouprace.feature.notification;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.model.NotificationModel;
import com.grouprace.core.notification.NotificationHelper;

public class NotificationFragment extends Fragment {
    private NotificationViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        EditText etTitle = (EditText) view.findViewById(R.id.edtTitle);
        EditText etMsg = (EditText) view.findViewById(R.id.edtMessage);
        Button btnSetAlarm = view.findViewById(R.id.btnSetAlarm);

        btnSetAlarm.setOnClickListener(v -> {
            String titleStr = etTitle.getText().toString().trim();
            String msgStr = etMsg.getText().toString().trim();
            NotificationModel notify = new NotificationModel(titleStr, msgStr);
            Log.d("Title", notify.getTitle());

            // Tạo một Intent để khi nhấn vào thông báo thì mở lại App
            Intent intent = requireContext().getPackageManager()
                    .getLaunchIntentForPackage(requireContext().getPackageName());

            NotificationHelper.showNotification(
                    requireContext(),        // Context
                    (int) System.currentTimeMillis(),   // ID của thông báo (số nguyên bất kỳ)
                    notify.getTitle(),       // Tiêu đề
                    notify.getMessage(),     // Nội dung
                    intent                   // Intent để mở app
            );

            Toast.makeText(getContext(), "Đang ép hiển thị thông báo...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}