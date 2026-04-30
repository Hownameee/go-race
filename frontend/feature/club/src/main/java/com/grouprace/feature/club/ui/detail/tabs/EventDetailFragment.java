package com.grouprace.feature.club.ui.detail.tabs;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.ClubStats;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.adapter.LeaderboardAdapter;

import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EventDetailFragment extends Fragment {

    private static final String ARG_CLUB_ID = "CLUB_ID";
    private static final String ARG_EVENT_ID = "EVENT_ID";

    private EventDetailViewModel viewModel;
    private LeaderboardAdapter adapter;
    private ProgressBar pbLoading;

    public EventDetailFragment() {
        super(R.layout.fragment_event_detail);
    }

    public static EventDetailFragment newInstance(int clubId, int eventId) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLUB_ID, clubId);
        args.putInt(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int clubId = getArguments() != null ? getArguments().getInt(ARG_CLUB_ID, -1) : -1;
        int eventId = getArguments() != null ? getArguments().getInt(ARG_EVENT_ID, -1) : -1;
        if (clubId == -1 || eventId == -1) return;

        viewModel = new ViewModelProvider(this).get(EventDetailViewModel.class);

        TopAppBarHelper.setupTopAppBar(view, new TopAppBarConfig.Builder()
                .setTitle("Event Detail")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back)
                .setOnLeftIconClick(v -> requireActivity().getSupportFragmentManager().popBackStack())
                .build());

        pbLoading = view.findViewById(R.id.pb_detail_loading);
        TextView tvTitle = view.findViewById(R.id.tv_detail_title);
        TextView tvDesc = view.findViewById(R.id.tv_detail_desc);
//        TextView tvTarget = view.findViewById(R.id.tv_detail_target);
        View layoutProgress = view.findViewById(R.id.layout_detail_progress);
        TextView tvProgressPercent = view.findViewById(R.id.tv_detail_progress_percent);
        TextView tvProgressSummary = view.findViewById(R.id.tv_detail_progress_summary);
        LinearProgressIndicator pbProgress = view.findViewById(R.id.pb_detail_progress);
        TextView tvStartTime = view.findViewById(R.id.tv_detail_start_time);
        TextView tvParticipants = view.findViewById(R.id.tv_detail_participants);
        RecyclerView rvLeaderboard = view.findViewById(R.id.rv_event_leaderboard);
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LeaderboardAdapter();
        rvLeaderboard.setAdapter(adapter);

        pbLoading.setVisibility(View.VISIBLE);
        // 1. Observe local metadata (Offline First)
        viewModel.getLocalEvent(clubId, eventId).observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                tvTitle.setText(event.getTitle());
                tvDesc.setText(event.getDescription());
                
                // Set initial progress from local sync
                double current = event.getGlobalDistance();
                double target = event.getTargetDistance();
                if (target > 0) {
                    layoutProgress.setVisibility(View.VISIBLE);
                    int percent = (int) Math.min((current / target) * 100, 100);
                    tvProgressPercent.setText(percent + "%");
                    tvProgressSummary.setText(String.format("%.2f / %.2f km", current, target));
                    pbProgress.setProgress(percent);
                }

                int pCount = event.getParticipantsCount();
                tvParticipants.setText(pCount + (pCount == 1 ? " participant" : " participants"));
                
                try {
                    java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US);
                    isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    java.util.Date startDate = isoFormat.parse(event.getStartTime());
                    java.util.Date endDate = isoFormat.parse(event.getEndTime());
                    if (startDate != null && endDate != null) {
                        java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.US);
                        tvStartTime.setText("Starts: " + displayFormat.format(startDate));
                    }
                } catch (Exception ignored) {}
            }
        });

        // 2. Fetch Live Stats (Leaderboard & Real-time progress)
        viewModel.syncEventStats(clubId, eventId).observe(getViewLifecycleOwner(), result -> {
            pbLoading.setVisibility(View.GONE);
            if (result instanceof Result.Success) {
                com.grouprace.core.model.EventStats stats = ((Result.Success<com.grouprace.core.model.EventStats>) result).data;
                
                // Refresh everything with live data
                tvTitle.setText(stats.getTitle());
                tvDesc.setText(stats.getDescription());

                int pCount = stats.getParticipantsCount();
                tvParticipants.setText(pCount + (pCount == 1 ? " participant" : " participants"));

                // Update progress bar with live global progress data
                double liveGlobal = stats.getTotalDistance();
                double liveTarget = stats.getTargetDistance();
                if (liveTarget > 0) {
                    layoutProgress.setVisibility(View.VISIBLE);
                    int livePercent = (int) Math.min((liveGlobal / liveTarget) * 100, 100);
                    tvProgressPercent.setText(livePercent + "%");
                    tvProgressSummary.setText(String.format("%.2f / %.2f km", liveGlobal, liveTarget));
                    pbProgress.setProgress(livePercent);
                }

                if (stats.getLeaderboard() != null) {
                    adapter.submitList(stats.getLeaderboard());
                }
            }
        });
    }
}
