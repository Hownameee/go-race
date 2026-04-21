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
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.adapter.LeaderboardAdapter;
import com.grouprace.feature.club.ui.detail.ClubDetailViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StatisticsFragment extends Fragment {

    private static final String ARG_CLUB_ID = "CLUB_ID";
    private ClubDetailViewModel viewModel;
    private LeaderboardAdapter adapter;
    private ProgressBar pbLoading;

    public StatisticsFragment() {
        super(R.layout.fragment_club_statistics);
    }

    public static StatisticsFragment newInstance(int clubId) {
        StatisticsFragment fragment = new StatisticsFragment();
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

        viewModel = new ViewModelProvider(requireActivity()).get(ClubDetailViewModel.class);

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
        viewModel.fetchClubStats(clubId).observe(getViewLifecycleOwner(), result -> {
            pbLoading.setVisibility(View.GONE);
            if (result instanceof Result.Success) {
                com.grouprace.core.model.ClubStats stats = ((Result.Success<com.grouprace.core.model.ClubStats>) result).data;
                
                // Format distance with thousand separator if needed, here just using simple format
                tvTotalDistance.setText(String.format(java.util.Locale.US, "%,.0f", stats.getTotalDistance()));
                
                tvTotalActivities.setText(String.valueOf(stats.getTotalActivities()));
                tvClubRecordDistance.setText(stats.getClubRecordDistanceStr());
                tvClubRecordDuration.setText(stats.getClubRecordDurationStr());
                
                tvPbDistance.setText(stats.getPersonalBestDistanceStr());
                tvPbDuration.setText(stats.getPersonalBestDurationStr());
                
                adapter.submitList(stats.getLeaderboard());
            } else if (result instanceof Result.Error) {
                String errorMsg = ((Result.Error<?>) result).message;
                Toast.makeText(getContext(), "Failed to load stats: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TopAppBarConfig getTopAppBarConfig() {
        return new TopAppBarConfig.Builder()
                .setTitle("Club Leaderboard")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back)
                .setOnLeftIconClick(v -> {
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                })
                .build();
    }
}
