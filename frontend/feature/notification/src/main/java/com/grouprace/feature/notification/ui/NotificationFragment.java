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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.NotificationModel;
import com.grouprace.core.notification.NotificationHelper;
import com.grouprace.feature.notification.R;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NotificationFragment extends Fragment {

    private NotificationViewModel viewModel;
    private int currentUserId;

    private EditText etTitle;
    private EditText etMsg;
    private Button btnSetAlarm;

    private Button btnFetchNotifications;


    private NotificationAdapter adapter;
    private Integer lastShownNotificationId = null;
    private boolean hasHydratedInitialList = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Get userId from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", 1);

        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        // Start socket
        viewModel.startSocket(currentUserId);

        // Initialize views
        etTitle = view.findViewById(R.id.edtTitle);
        etMsg = view.findViewById(R.id.edtMessage);
        btnSetAlarm = view.findViewById(R.id.btnSetAlarm);
        btnFetchNotifications = view.findViewById(R.id.btnFetchNotification);

        btnSetAlarm.setOnClickListener(v -> createManualNotification());

        btnFetchNotifications.setOnClickListener(v -> viewModel.refreshNotifications());

        // Setup RecyclerView
        adapter = new NotificationAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Use one observer for list + local notification signal.
        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications != null && !notifications.isEmpty()) {
                adapter.submitList(notifications);  // ← show all items
                Log.d("NOTIFICATIONS", "Total items: " + notifications.size());
                maybeShowSystemNotification(notifications.get(0));
            } else {
                adapter.submitList(java.util.Collections.emptyList());
                Log.d("NOTIFICATIONS", "No notifications found");
            }
        });

        return view;
    }

    private void maybeShowSystemNotification(NotificationModel latest) {
        if (latest == null) return;
        if (!hasHydratedInitialList) {
            hasHydratedInitialList = true;
            lastShownNotificationId = latest.getId();
            return;
        }
        if (lastShownNotificationId != null && latest.getId() == lastShownNotificationId) {
            return;
        }
        lastShownNotificationId = latest.getId();

        Intent intent = requireContext().getPackageManager()
                .getLaunchIntentForPackage(requireContext().getPackageName());
        if (intent == null) return;

        NotificationHelper.showNotification(
                requireContext(),
                (int) System.currentTimeMillis(),
                latest.getTitle(),
                latest.getMessage(),
                intent
        );

        Toast.makeText(getContext(), "Nhận notification mới!", Toast.LENGTH_SHORT).show();
    }

    private void createManualNotification() {
        String titleStr = etTitle.getText().toString().trim();
        String msgStr = etMsg.getText().toString().trim();

        if (titleStr.isEmpty() || msgStr.isEmpty()) {
            Toast.makeText(getContext(), "Nhập đầy đủ title và message", Toast.LENGTH_SHORT).show();
            return;
        }

        NotificationModel manualNotification = new NotificationModel(
                (int) (System.currentTimeMillis() / 1000),
                currentUserId,
                "system",
                null,
                null,
                titleStr,
                msgStr,
                ""
        );

        viewModel.addNotification(manualNotification);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.disconnect();
    }
}