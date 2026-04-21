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

import com.grouprace.core.common.result.Result;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.adapter.LeaderboardAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ClubStatisticsFragment extends Fragment {

    private static final String ARG_CLUB_ID = "CLUB_ID";
    private ClubStatisticsViewModel viewModel;
    private LeaderboardAdapter adapter;
    private ProgressBar pbLoading;

    public ClubStatisticsFragment() {
        super(R.layout.fragment_club_statistics);
    }

    public static ClubStatisticsFragment newInstance(int clubId) {
        ClubStatisticsFragment fragment = new ClubStatisticsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLUB_ID, clubId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int clubId = getArguments() != null ? getArguments().getInt(ARG_CLUB_ID, -1) : -1;
        if (clubId == -1) return;

        viewModel = new ViewModelProvider(this).get(ClubStatisticsViewModel.class);

        TopAppBarHelper.setupTopAppBar(view, getTopAppBarConfig());

        pbLoading = view.findViewById(R.id.pb_loading);
        RecyclerView rvLeaderboard = view.findViewById(R.id.rv_leaderboard);
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LeaderboardAdapter();
        rvLeaderboard.setAdapter(adapter);

        TextView tvTotalDistance = view.findViewById(R.id.tv_stat_total_distance);
        TextView tvTotalActivities = view.findViewById(R.id.tv_stat_total_activities);
        TextView tvClubRecordDistance = view.findViewById(R.id.tv_stat_club_record_distance);
        TextView tvClubRecordDuration = view.findViewById(R.id.tv_stat_club_record_duration);
        TextView tvPbDistance = view.findViewById(R.id.tv_pb_distance);
        TextView tvPbDuration = view.findViewById(R.id.tv_pb_duration);

        pbLoading.setVisibility(View.VISIBLE);
        
        // 1. Trigger background sync
        viewModel.syncClubStats(clubId).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success || result instanceof Result.Error) {
                pbLoading.setVisibility(View.GONE);
                if (result instanceof Result.Error) {
                    String errorMsg = ((Result.Error<?>) result).message;
                    Toast.makeText(getContext(), "Failed to sync stats: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 2. Observe local ClubEntity for stats
        viewModel.getLocalClubById(clubId).observe(getViewLifecycleOwner(), club -> {
            if (club != null && club.getClubId() == clubId) {
                tvTotalDistance.setText(String.format(java.util.Locale.US, "%,.0f", club.getTotalDistance()));
                tvTotalActivities.setText(String.valueOf(club.getTotalActivities()));
                tvClubRecordDistance.setText(club.getClubRecordDistanceStr() != null ? club.getClubRecordDistanceStr() : "0 km");
                tvClubRecordDuration.setText(club.getClubRecordDurationStr() != null ? club.getClubRecordDurationStr() : "0h");
                tvPbDistance.setText(club.getPersonalBestDistanceStr() != null ? club.getPersonalBestDistanceStr() : "0 km");
                tvPbDuration.setText(club.getPersonalBestDurationStr() != null ? club.getPersonalBestDurationStr() : "0h");
            }
        });

        // 3. Observe local Leaderboard
        viewModel.getLocalLeaderboard(clubId).observe(getViewLifecycleOwner(), leaderboard -> {
            if (leaderboard != null) {
                adapter.submitList(leaderboard);
            }
        });
    }

    private TopAppBarConfig getTopAppBarConfig() {
        return new TopAppBarConfig.Builder()
                .setTitle("Club Statistics")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back)
                .setOnLeftIconClick(v -> {
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                })
                .build();
    }
}
