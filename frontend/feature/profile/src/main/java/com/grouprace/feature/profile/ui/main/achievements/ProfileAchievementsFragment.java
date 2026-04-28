package com.grouprace.feature.profile.ui.main.achievements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.feature.profile.R;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileAchievementsFragment extends Fragment {
    private static final String ARG_IS_SELF = "arg_is_self";
    private static final String ARG_USER_ID = "arg_user_id";

    private ProfileAchievementsViewModel viewModel;
    private AchievementAdapter adapter;
    private TextView loadingState;
    private TextView errorState;
    private TextView summaryText;
    private RecyclerView recyclerView;

    public static ProfileAchievementsFragment newInstance(boolean isSelf, int userId) {
        ProfileAchievementsFragment fragment = new ProfileAchievementsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_SELF, isSelf);
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_achievements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean isSelf = getArguments() == null || getArguments().getBoolean(ARG_IS_SELF, true);
        int userId = getArguments() == null ? -1 : getArguments().getInt(ARG_USER_ID, -1);
        viewModel = new ViewModelProvider(this).get(ProfileAchievementsViewModel.class);
        viewModel.initialize(isSelf, userId);

        ImageButton backButton = view.findViewById(R.id.profile_achievements_back_button);
        loadingState = view.findViewById(R.id.profile_achievements_loading_state);
        errorState = view.findViewById(R.id.profile_achievements_error_state);
        summaryText = view.findViewById(R.id.profile_achievements_summary);
        recyclerView = view.findViewById(R.id.profile_achievements_recycler_view);

        backButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        adapter = new AchievementAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerView.setAdapter(adapter);

        observeAchievements();
    }

    private void observeAchievements() {
        viewModel.getAchievementSummary().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                loadingState.setVisibility(View.VISIBLE);
                errorState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                summaryText.setVisibility(View.GONE);
            } else if (result instanceof Result.Success) {
                loadingState.setVisibility(View.GONE);
                errorState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                summaryText.setVisibility(View.VISIBLE);

                RecordProfileStatisticsResponse response = ((Result.Success<RecordProfileStatisticsResponse>) result).data;
                int totalActivities = extractTotalActivities(response);
                List<AchievementItem> achievements = AchievementHelper.buildAchievements(totalActivities);
                adapter.submitList(achievements);
                summaryText.setText(AchievementHelper.countUnlocked(achievements) + " / "
                        + AchievementHelper.MILESTONES.length + " achievements unlocked");
            } else if (result instanceof Result.Error) {
                loadingState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                summaryText.setVisibility(View.GONE);
                errorState.setVisibility(View.VISIBLE);
                String message = ((Result.Error<RecordProfileStatisticsResponse>) result).message;
                errorState.setText(message != null ? message : "Unable to load achievements.");
            }
        });
    }

    private int extractTotalActivities(@Nullable RecordProfileStatisticsResponse response) {
        if (response == null || response.getAllTime() == null) {
            return 0;
        }
        return (int) Math.floor(response.getAllTime().getTotalActivities());
    }
}
