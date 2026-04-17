package com.grouprace.feature.notification.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.NotificationModel;
import com.grouprace.feature.notification.R;
import com.grouprace.feature.notification.ui.apdater.NotificationAdapter;

import java.util.Collections;
import java.util.List;

import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NotificationFragment extends Fragment {

    private NotificationViewModel viewModel;
    private NotificationAdapter adapter;

    private ProgressBar progressBar;

    private Integer lastShownNotificationId = null;
    private boolean hasHydratedInitialList = false;

    private TextView tvEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        TopAppBarHelper.setupTopAppBar(view, getTopAppBarConfig());

        // RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rv_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnNotificationClickListener(notification -> {
            if (!notification.isRead()) {
                viewModel.markAsRead(notification);
            }
            Intent intent = new Intent();
            intent.setClassName(
                    requireContext(),
                    "com.grouprace.gorace.MainActivity"
            );

            intent.putExtra("type", notification.getType());
            intent.putExtra("actor_id", String.valueOf(notification.getActorId()));
            intent.putExtra("activity_id", String.valueOf(notification.getActivityId()));

            startActivity(intent);
        });

        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tv_empty);

        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        observeNotifications();

        return view;
    }

    private void observeNotifications() {
        viewModel.getNotifications().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            } else if (result instanceof Result.Success) {
                progressBar.setVisibility(View.GONE);
                List<NotificationModel> notifications =
                        ((Result.Success<List<NotificationModel>>) result).data;
                handleSuccessState(notifications);
            } else if (result instanceof Result.Error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void handleSuccessState(List<NotificationModel> notifications) {
        if (notifications != null && !notifications.isEmpty()) {
            tvEmpty.setVisibility(View.GONE);
            adapter.submitList(notifications);
            NotificationModel latest = Collections.max(
                    notifications,
                    (a, b) -> a.getId() - b.getId()
            );
//            maybeShowSystemNotification(latest);
            Log.d("NotificationFragment", "Notifications count: " + notifications.size());
        } else {
            adapter.submitList(Collections.emptyList());
            tvEmpty.setVisibility(View.VISIBLE);
            Log.d("NotificationFragment", "No notifications found");
        }
    }
//    private void maybeShowSystemNotification(NotificationModel latest) {
//        if (latest == null) return;
//
//        if (!hasHydratedInitialList) {
//            hasHydratedInitialList = true;
//            lastShownNotificationId = latest.getId();
//            return;
//        }
//
//        if (lastShownNotificationId != null &&
//                latest.getId() == lastShownNotificationId) {
//            return;
//        }
//
//        lastShownNotificationId = latest.getId();
//
//        Intent intent = new Intent(requireContext(), NotificationFragment.class);
//        com.grouprace.core.notification.NotificationHelper.showNotification(
//                requireContext(),
//                latest.getId(),
//                latest.getTitle(),
//                latest.getMessage(),
//                intent
//        );
//    }

    private TopAppBarConfig getTopAppBarConfig() {
        return new TopAppBarConfig.Builder()
                .setTitle("Notifications")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back, v -> {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                })
                .build();
    }
}
