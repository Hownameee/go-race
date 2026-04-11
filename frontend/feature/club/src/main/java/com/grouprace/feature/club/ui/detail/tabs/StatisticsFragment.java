package com.grouprace.feature.club.ui.detail.tabs;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.ClubStats;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.ClubDetailViewModel;
import com.grouprace.feature.club.ui.adapter.ClubLeaderboardAdapter;

public class StatisticsFragment extends Fragment {

    private ClubDetailViewModel viewModel;
    private ClubLeaderboardAdapter adapter;
    
    // UI components mapped from XML
    private TextView tvTotalDistance, tvTotalActivities;
    private TextView tvClubRecordDistance, tvClubRecordDuration;
    private TextView tvPersonalBestDistance, tvPersonalBestDuration;

    public StatisticsFragment() {
        super(R.layout.fragment_club_detail_statistics);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireParentFragment()).get(ClubDetailViewModel.class);
        
        tvTotalDistance = view.findViewById(R.id.text_total_distance);
        tvTotalActivities = view.findViewById(R.id.text_total_activities);
        tvClubRecordDistance = view.findViewById(R.id.text_club_record_distance);
        tvClubRecordDuration = view.findViewById(R.id.text_club_record_duration);
        tvPersonalBestDistance = view.findViewById(R.id.text_personal_best_distance);
        tvPersonalBestDuration = view.findViewById(R.id.text_personal_best_duration);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_leaderboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClubLeaderboardAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getClubStats().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                ClubStats stats = ((Result.Success<ClubStats>) result).data;
                bindStats(stats);
                adapter.submitList(stats.getLeaderboard());
            }
        });
    }

    private void bindStats(ClubStats stats) {
        tvTotalDistance.setText(String.format("%.1f km\nTotal Distance", stats.getTotalDistance()));
        tvTotalActivities.setText(stats.getTotalActivities() + "\nActivities");
        
        tvClubRecordDistance.setText("Longest Distance: " + stats.getClubRecordDistanceStr());
        tvClubRecordDuration.setText("Longest Duration: " + stats.getClubRecordDurationStr());
        
        tvPersonalBestDistance.setText("Longest Distance: " + stats.getPersonalBestDistanceStr());
        tvPersonalBestDuration.setText("Longest Duration: " + stats.getPersonalBestDurationStr());
    }
}
