package com.grouprace.feature.notification.ui;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.NotificationModel;
import com.grouprace.feature.notification.R;
import com.grouprace.feature.notification.ui.apdater.NotificationAdapter;

import java.util.Collections;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NotificationFragment extends Fragment {

    private NotificationViewModel viewModel;
    private NotificationAdapter adapter;
    private int currentUserId;

    private View loadMoreLoading;
    private View loadMoreError;
    private Button loadMoreRetry;

    private Integer lastShownNotificationId = null;
    private boolean hasHydratedInitialList = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Request permission Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.POST_NOTIFICATIONS) !=
                        android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        // Lấy userId
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }

        // Setup RecyclerView & Adapter
        RecyclerView recyclerView = view.findViewById(R.id.rv_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter();
        recyclerView.setAdapter(adapter);

        // Click listener
        adapter.setOnNotificationClickListener(notification ->
                Toast.makeText(getContext(), "Click: " + notification.getTitle(), Toast.LENGTH_SHORT).show()
        );

        setupLoadMoreViews(view);

        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        // Register FCM token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) return;
            String token = task.getResult();

            if (token != null && !token.isEmpty()) {
                if (currentUserId == -1) {
                    FirebaseMessaging.getInstance().subscribeToTopic("system-notifications");
                } else {
                    viewModel.registerDeviceToken(currentUserId, token);
                    FirebaseMessaging.getInstance().subscribeToTopic("system-notifications");
                }
            }
        });

        observeNotifications();

        return view;
    }

    private void setupLoadMoreViews(View view) {
        View loadMoreFooter = LayoutInflater.from(requireContext()).inflate(R.layout.footer_load_more, null, false);
        loadMoreLoading = loadMoreFooter.findViewById(R.id.ll_load_more_loading);
        loadMoreError = loadMoreFooter.findViewById(R.id.ll_load_more_error);
        loadMoreRetry = loadMoreFooter.findViewById(R.id.btn_load_more_retry);

        loadMoreRetry.setOnClickListener(v -> viewModel.refreshNotifications());

        // Nếu RecyclerView hỗ trợ footer, bạn có thể add ở đây
        // Hoặc dùng Layout riêng quản lý load more
        setLoadMoreVisibility(false, false);
    }

    private void observeNotifications() {
        viewModel.getNotifications().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                handleLoadingState();
            } else if (result instanceof Result.Success) {
                List<NotificationModel> notifications = ((Result.Success<List<NotificationModel>>) result).data;
                handleSuccessState(notifications);
            } else if (result instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) result;
                handleErrorState(error.message);
            }
        });
    }

    private void handleLoadingState() {
        if (adapter.getItemCount() == 0) {
            // show full screen loading if needed
            Log.d("NotificationFragment", "Loading notifications...");
        } else {
            setLoadMoreVisibility(true, false);
        }
    }

    private void handleSuccessState(List<NotificationModel> notifications) {
        setLoadMoreVisibility(false, false);
        if (notifications != null && !notifications.isEmpty()) {
            adapter.submitList(notifications);
            maybeShowSystemNotification(notifications.get(0));
            Log.d("NotificationFragment", "Notifications count: " + notifications.size());
        } else {
            adapter.submitList(Collections.emptyList());
            Log.d("NotificationFragment", "No notifications found");
        }
    }

    private void handleErrorState(String message) {
        if (adapter.getItemCount() == 0) {
            Toast.makeText(getContext(), "Lỗi tải notifications: " + message, Toast.LENGTH_SHORT).show();
        } else {
            setLoadMoreVisibility(false, true);
        }
    }

    private void maybeShowSystemNotification(NotificationModel latest) {
        if (latest == null) return;
        if (!hasHydratedInitialList) {
            hasHydratedInitialList = true;
            lastShownNotificationId = latest.getId();
            return;
        }
        if (lastShownNotificationId != null && latest.getId() == lastShownNotificationId) return;
        lastShownNotificationId = latest.getId();
        Toast.makeText(getContext(), "Nhận notification mới!", Toast.LENGTH_SHORT).show();
    }

    private void setLoadMoreVisibility(boolean isLoading, boolean isError) {
        if (loadMoreLoading != null)
            loadMoreLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (loadMoreError != null)
            loadMoreError.setVisibility(isError ? View.VISIBLE : View.GONE);
    }
}