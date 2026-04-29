package com.grouprace.feature.profile.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.ProfileOverview;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.profile.R;
import com.grouprace.feature.profile.ui.main.achievements_preview.ProfileAchievementsPreviewFragment;
import com.grouprace.feature.profile.ui.main.clubs_preview.ProfileClubsPreviewFragment;
import com.grouprace.feature.profile.ui.main.components.ProfileHeaderComponent;
import com.grouprace.feature.profile.ui.main.components.ProfileScreenConfig;
import com.grouprace.feature.profile.ui.main.links.ProfileLinksFragment;
import com.grouprace.feature.profile.ui.main.overview.ProfileOverviewFragment;
import com.grouprace.feature.profile.ui.main.stats.ProfileStatsFragment;
import com.grouprace.feature.profile.ui.main.stats.ProfileStatsOwner;
import com.grouprace.feature.profile.ui.main.streak.ProfileStreakFragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserProfileFragment extends Fragment implements ProfileStatsOwner {
    private static final String ARG_USER_ID = "arg_user_id";

    @Inject
    AppNavigator navigator;

    private UserProfileViewModel viewModel;
    private ProfileScreenConfig screenConfig;
    private TextView profileMessage;
    private ProfileOverview currentOverview;

    public UserProfileFragment() {
        super(R.layout.fragment_profile);
    }

    public static UserProfileFragment newInstance(int userId) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int userId = getArguments() != null ? getArguments().getInt(ARG_USER_ID, -1) : -1;
        viewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        viewModel.initialize(userId);
        screenConfig = new ProfileScreenConfig(userId, false);
        profileMessage = view.findViewById(R.id.profile_message);

        setupHeader(view);
        observeOverview();
        attachStatsFragment();
        loadProfileData();
    }

    private void setupHeader(View view) {
        ProfileHeaderComponent headerComponent = new ProfileHeaderComponent(view);
        headerComponent.bind(screenConfig, new ProfileHeaderComponent.Listener() {
            @Override
            public void onBackClicked() {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }

            @Override
            public void onSearchClicked() {
                navigator.navigateToSearch(UserProfileFragment.this);
            }

            @Override
            public void onSettingsClicked() {
            }
        });
    }

    private void attachStatsFragment() {
        if (getChildFragmentManager().findFragmentById(R.id.profile_stats_fragment_container) == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.profile_stats_fragment_container, ProfileStatsFragment.newInstance())
                    .commit();
        }
    }

    private void loadProfileData() {
        profileMessage.setVisibility(View.GONE);
        viewModel.loadUserOverview();
        String selectedActivity = viewModel.getSelectedActivityType().getValue();
        viewModel.loadWeeklySummary(selectedActivity != null
                ? selectedActivity
                : UserProfileViewModel.ACTIVITY_RUNNING);
    }

    private void attachProfileSections(ProfileOverview overview) {
        if (overview == null) {
            return;
        }

        int userId = overview.getUserId();
        String profileName = overview.getFullname();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.profile_overview_fragment_container, ProfileOverviewFragment.newInstance(userId, false))
                .replace(R.id.profile_streak_fragment_container, ProfileStreakFragment.newInstance(userId, false))
                .replace(R.id.profile_links_fragment_container, ProfileLinksFragment.newInstance(userId, profileName, false))
                .replace(R.id.profile_achievements_preview_fragment_container, ProfileAchievementsPreviewFragment.newInstance(userId, false))
                .replace(R.id.profile_clubs_preview_fragment_container, ProfileClubsPreviewFragment.newInstance(userId, profileName, false))
                .commit();
    }

    private void observeOverview() {
        viewModel.getProfileOverview().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                profileMessage.setVisibility(View.VISIBLE);
                profileMessage.setText("Loading profile overview...");
            } else if (result instanceof Result.Success) {
                profileMessage.setVisibility(View.GONE);
                currentOverview = ((Result.Success<ProfileOverview>) result).data;
                attachProfileSections(currentOverview);
            } else if (result instanceof Result.Error) {
                Result.Error<ProfileOverview> error = (Result.Error<ProfileOverview>) result;
                profileMessage.setVisibility(View.VISIBLE);
                profileMessage.setText(error.message != null
                        ? error.message
                        : "Failed to load profile overview.");
            }
        });
    }

    @Override
    public androidx.lifecycle.LiveData<Result<WeeklyRecordSummary>> getWeeklySummaryLiveData() {
        return viewModel.getWeeklySummary();
    }

    @Override
    public androidx.lifecycle.LiveData<String> getSelectedActivityTypeLiveData() {
        return viewModel.getSelectedActivityType();
    }

    @Override
    public void onSelectActivityType(String activityType) {
        viewModel.selectActivityType(activityType);
    }
}
