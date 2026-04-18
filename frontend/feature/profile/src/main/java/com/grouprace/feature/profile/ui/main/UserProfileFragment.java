package com.grouprace.feature.profile.ui.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.ProfileOverview;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.core.network.model.record.RecordStreakResponse;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.system.ui.ViewHelper;
import com.grouprace.feature.profile.R;
import com.grouprace.feature.profile.ui.follow.FollowListFragment;
import com.grouprace.feature.profile.ui.main.achievements.AchievementHelper;
import com.grouprace.feature.profile.ui.main.achievements.AchievementItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserProfileFragment extends Fragment implements ProfileStatsOwner {
    private static final String ARG_USER_ID = "arg_user_id";

    @Inject
    AppNavigator navigator;

    private UserProfileViewModel viewModel;
    private ImageButton profileHeaderBackButton;
    private ImageButton profileSearchButton;
    private ImageButton profileSettingButton;
    private TextView profileMessage;
    private Button profileLoginButton;
    private LinearLayout profileRootContent;
    private TextView profileFullname;
    private ImageView profileAvatar;
    private TextView profileBio;
    private TextView profileCity;
    private TextView profileCountry;
    private TextView profileTotalFollowings;
    private TextView profileTotalFollowers;
    private View followingLink;
    private View followersLink;
    private Button followButton;
    private View activitiesLink;
    private View statisticsLink;
    private View achievementsLink;
    private View routesLink;
    private View segmentsLink;
    private View bestEffortsLink;
    private View postsLink;
    private FragmentContainerView statsContainer;
    private TextView achievementsCount;
    private LinearLayout achievementsPreviewContainer;
    private TextView streakStatus;
    private ImageView streakFireIcon;
    private TextView currentStreak;
    private TextView longestStreak;
    private TextView totalActiveDays;
    private ProfileOverview currentOverview;

    public static UserProfileFragment newInstance(int userId) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int userId = getArguments() != null ? getArguments().getInt(ARG_USER_ID, -1) : -1;
        viewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        viewModel.initialize(userId);

        profileHeaderBackButton = view.findViewById(R.id.profile_back_button);
        profileSearchButton = view.findViewById(R.id.profile_search_button);
        profileSettingButton = view.findViewById(R.id.profile_setting_button);
        profileMessage = view.findViewById(R.id.profile_message);
        profileLoginButton = view.findViewById(R.id.profile_login_button);
        profileRootContent = view.findViewById(R.id.profile_root_content);
        profileAvatar = view.findViewById(R.id.avatar);
        profileFullname = view.findViewById(R.id.profile_fullname);
        profileBio = view.findViewById(R.id.profile_bio);
        profileCity = view.findViewById(R.id.profile_city);
        profileCountry = view.findViewById(R.id.profile_country);
        profileTotalFollowings = view.findViewById(R.id.profile_total_followings);
        profileTotalFollowers = view.findViewById(R.id.profile_total_followers);
        followingLink = view.findViewById(R.id.profile_following_link);
        followersLink = view.findViewById(R.id.profile_followers_link);
        followButton = view.findViewById(R.id.profile_edit_button);
        activitiesLink = view.findViewById(R.id.profile_activities_link);
        statisticsLink = view.findViewById(R.id.profile_statistics_link);
        achievementsLink = view.findViewById(R.id.profile_achievements_link);
        routesLink = view.findViewById(R.id.profile_routes_link);
        segmentsLink = view.findViewById(R.id.profile_segments_link);
        bestEffortsLink = view.findViewById(R.id.profile_best_efforts_link);
        postsLink = view.findViewById(R.id.profile_posts_link);
        statsContainer = view.findViewById(R.id.profile_stats_fragment_container);
        achievementsCount = view.findViewById(R.id.profile_achievements_count);
        achievementsPreviewContainer = view.findViewById(R.id.profile_achievements_preview_container);
        streakStatus = view.findViewById(R.id.profile_streak_status);
        streakFireIcon = view.findViewById(R.id.profile_streak_fire_icon);
        currentStreak = view.findViewById(R.id.profile_current_streak);
        longestStreak = view.findViewById(R.id.profile_longest_streak);
        totalActiveDays = view.findViewById(R.id.profile_total_active_days);

        profileSettingButton.setVisibility(View.GONE);
        profileHeaderBackButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        profileSearchButton.setOnClickListener(v -> navigator.navigateToSearch(this));
        followingLink.setOnClickListener(v -> openFollowList(FollowListFragment.TAB_FOLLOWING));
        followersLink.setOnClickListener(v -> openFollowList(FollowListFragment.TAB_FOLLOWERS));
        followButton.setOnClickListener(v -> toggleFollow());
        activitiesLink.setOnClickListener(v -> openActivities());
        statisticsLink.setOnClickListener(v -> openStatistics());
        achievementsLink.setOnClickListener(v -> openAchievements());
        routesLink.setOnClickListener(v -> openComingSoon("Routes"));
        segmentsLink.setOnClickListener(v -> openComingSoon("Segments"));
        bestEffortsLink.setOnClickListener(v -> openComingSoon("Best Efforts"));
        postsLink.setOnClickListener(v -> openComingSoon("Posts"));

        observeOverview();
        observeAchievementSummary();
        observeStreakSummary();
        attachStatsFragment();
        loadProfileData();
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
        profileLoginButton.setVisibility(View.GONE);
        for (int i = 2; i < profileRootContent.getChildCount(); i++) {
            profileRootContent.getChildAt(i).setVisibility(View.VISIBLE);
        }
        viewModel.loadUserOverview();
        viewModel.loadAchievementSummary();
        viewModel.loadStreakSummary();
        String selectedActivity = viewModel.getSelectedActivityType().getValue();
        viewModel.loadWeeklySummary(selectedActivity != null
                ? selectedActivity
                : UserProfileViewModel.ACTIVITY_RUNNING);
    }

    private void openActivities() {
        if (currentOverview == null) {
            Toast.makeText(requireContext(), "Profile is still loading.", Toast.LENGTH_SHORT).show();
            return;
        }
        navigator.openProfileActivities(this, currentOverview.getUserId(), currentOverview.getFullname(), false);
    }

    private void openFollowList(String initialTab) {
        if (currentOverview == null) {
            Toast.makeText(requireContext(), "Profile is still loading.", Toast.LENGTH_SHORT).show();
            return;
        }
        navigator.openProfileFollowList(this, currentOverview.getUserId(), currentOverview.getFullname(), false, initialTab);
    }

    private void openComingSoon(String title) {
        navigator.openProfileComingSoon(this, title);
    }

    private void openStatistics() {
        if (currentOverview == null) {
            Toast.makeText(requireContext(), "Profile is still loading.", Toast.LENGTH_SHORT).show();
            return;
        }
        navigator.openProfileStatistics(this, currentOverview.getUserId(), false);
    }

    private void openAchievements() {
        if (currentOverview == null) {
            Toast.makeText(requireContext(), "Profile is still loading.", Toast.LENGTH_SHORT).show();
            return;
        }
        navigator.openProfileAchievements(this, currentOverview.getUserId(), false);
    }

    private void toggleFollow() {
        if (currentOverview == null) {
            return;
        }

        followButton.setEnabled(false);
        LiveData<Result<Boolean>> action = currentOverview.isFollowing()
                ? viewModel.unfollowUser()
                : viewModel.followUser();

        action.observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                boolean nextFollowing = !currentOverview.isFollowing();
                viewModel.applyFollowingState(nextFollowing);
                followButton.setEnabled(true);
            } else if (result instanceof Result.Error) {
                followButton.setEnabled(true);
                String message = ((Result.Error<Boolean>) result).message;
                Toast.makeText(requireContext(), message != null ? message : "Follow action failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeOverview() {
        viewModel.getProfileOverview().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                profileMessage.setVisibility(View.VISIBLE);
                profileMessage.setText("Loading profile overview...");
            } else if (result instanceof Result.Success) {
                profileMessage.setVisibility(View.GONE);
                bindOverview(((Result.Success<ProfileOverview>) result).data);
            } else if (result instanceof Result.Error) {
                Result.Error<ProfileOverview> error = (Result.Error<ProfileOverview>) result;
                profileMessage.setVisibility(View.VISIBLE);
                profileMessage.setText(error.message != null
                        ? error.message
                        : "Failed to load profile overview.");
            }
        });
    }

    private void bindOverview(@Nullable ProfileOverview overview) {
        if (overview == null) {
            return;
        }

        currentOverview = overview;
        ViewHelper.bindOptionalText(profileFullname, overview.getFullname());
        ViewHelper.bindOptionalText(profileBio, overview.getBio());
        ViewHelper.bindOptionalText(profileCity, overview.getCity());
        ViewHelper.bindOptionalText(profileCountry, overview.getCountry());
        profileTotalFollowings.setText(String.valueOf(overview.getTotalFollowings()));
        profileTotalFollowers.setText(String.valueOf(overview.getTotalFollowers()));
        updateFollowButton(overview.isFollowing());
        loadAvatarPreview(overview.getAvatarUrl());
    }

    private void observeAchievementSummary() {
        viewModel.getAchievementSummary().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                bindAchievements(((Result.Success<RecordProfileStatisticsResponse>) result).data);
            }
        });
    }

    private void observeStreakSummary() {
        viewModel.getStreakSummary().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                bindStreakSummary(((Result.Success<RecordStreakResponse>) result).data);
            }
        });
    }

    private void bindAchievements(@Nullable RecordProfileStatisticsResponse response) {
        int totalActivities = 0;
        if (response != null && response.getAllTime() != null) {
            totalActivities = (int) Math.floor(response.getAllTime().getTotalActivities());
        }

        List<AchievementItem> allAchievements = AchievementHelper.buildAchievements(totalActivities);
        List<AchievementItem> previewAchievements = AchievementHelper.buildPreviewAchievements(totalActivities);
        achievementsCount.setText(AchievementHelper.countUnlocked(allAchievements) + " / " + AchievementHelper.MILESTONES.length);
        populateAchievementPreview(previewAchievements);
    }

    private void populateAchievementPreview(@NonNull List<AchievementItem> previewAchievements) {
        achievementsPreviewContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (AchievementItem item : previewAchievements) {
            View itemView = inflater.inflate(R.layout.item_profile_achievement, achievementsPreviewContainer, false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
            );
            itemView.setLayoutParams(params);

            View badgeContainer = itemView.findViewById(R.id.achievement_badge_container);
            TextView milestoneValue = itemView.findViewById(R.id.achievement_milestone_value);
            TextView milestoneLabel = itemView.findViewById(R.id.achievement_milestone_label);

            badgeContainer.setBackgroundResource(item.isUnlocked()
                    ? R.drawable.bg_achievement_hexagon_unlocked
                    : R.drawable.bg_achievement_hexagon_locked);
            milestoneValue.setText(String.valueOf(item.getMilestone()));
            milestoneLabel.setText(AchievementHelper.formatMilestoneLabel(item.getMilestone()));
            milestoneValue.setAlpha(item.isUnlocked() ? 1f : 0.55f);
            milestoneLabel.setAlpha(item.isUnlocked() ? 1f : 0.55f);

            achievementsPreviewContainer.addView(itemView);
        }
    }

    private void bindStreakSummary(@Nullable RecordStreakResponse streak) {
        if (streak == null) {
            return;
        }

        currentStreak.setText(String.valueOf(streak.getCurrentStreakDays()));
        longestStreak.setText(String.valueOf(streak.getLongestStreakDays()));
        totalActiveDays.setText(String.valueOf(streak.getTotalActiveDays()));
        streakFireIcon.setImageResource(streak.isTodayHasRecord()
                ? R.drawable.ic_streak_fire_active
                : R.drawable.ic_streak_fire_inactive);

        String status;
        if (streak.getCurrentStreakDays() <= 0) {
            status = "No active streak right now.";
        } else if (streak.isTodayHasRecord()) {
            status = "This athlete has an active streak today.";
        } else {
            status = "Their streak is still alive if they record today.";
        }
        streakStatus.setText(status);
    }

    private void updateFollowButton(boolean isFollowing) {
        followButton.setText(isFollowing ? "Following" : "Follow");
        int background = isFollowing
                ? com.grouprace.core.system.R.drawable.bg_button_secondary_rounded
                : com.grouprace.core.system.R.drawable.bg_button_rounded;
        followButton.setBackgroundResource(background);
        followButton.setTextColor(requireContext().getColor(android.R.color.black));
    }

    private void loadAvatarPreview(@Nullable String avatarUrl) {
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            profileAvatar.setImageDrawable(null);
            return;
        }

        new Thread(() -> {
            try (InputStream inputStream = new URL(avatarUrl).openStream()) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null && isAdded()) {
                    requireActivity().runOnUiThread(() -> profileAvatar.setImageBitmap(bitmap));
                }
            } catch (IOException ignored) {
            }
        }).start();
    }

    @Override
    public androidx.lifecycle.LiveData<Result<com.grouprace.core.model.Profile.WeeklyRecordSummary>> getWeeklySummaryLiveData() {
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
