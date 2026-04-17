//package com.grouprace.feature.profile.ui;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentContainerView;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.grouprace.core.common.result.Result;
//import com.grouprace.core.model.Profile.ProfileOverview;
//import com.grouprace.core.navigation.AppNavigator;
//import com.grouprace.core.system.ui.ViewHelper;
//import com.grouprace.feature.profile.R;
//
//import javax.inject.Inject;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//
//import dagger.hilt.android.AndroidEntryPoint;
//
//@AndroidEntryPoint
//public class ProfileFragment extends Fragment {
//
//    @Inject
//    AppNavigator navigator;
//
//    private ProfileViewModel viewModel;
//
//    // Header
//    private ImageButton profileHeaderBackButton;
//    private ImageButton profileSettingButton;
//
//    private TextView profileMessage;
//    private Button profileLoginButton;
//
//    // Overview
//    private LinearLayout profileRootContent;
//    private TextView profileFullname;
//    private ImageView profileAvatar;
//    private TextView profileCity;
//    private TextView profileCountry;
//    private TextView profileTotalFollowings;
//    private TextView profileTotalFollowers;
//    private Button editProfileButton;
//    private View activitiesLink;
//    private View statisticsLink;
//    private View routesLink;
//    private View segmentsLink;
//    private View bestEffortsLink;
//    private View postsLink;
//    private View gearLink;
//    private FragmentContainerView statsContainer;
//
//    public static ProfileFragment newInstance(int userId) {
//        ProfileFragment fragment = new ProfileFragment();
//
//        Bundle args = new Bundle();
//        args.putInt("user_id", userId);
//
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_profile, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
//
//        // Header
//        profileHeaderBackButton = view.findViewById(R.id.profile_back_button);
//        profileSettingButton = view.findViewById(R.id.profile_setting_button);
//
//        profileMessage = view.findViewById(R.id.profile_message);
//        profileLoginButton = view.findViewById(R.id.profile_login_button);
//
//        profileRootContent = view.findViewById(R.id.profile_root_content);
//        profileAvatar = view.findViewById(R.id.avatar);
//        profileFullname = view.findViewById(R.id.profile_fullname);
//        profileCity = view.findViewById(R.id.profile_city);
//        profileCountry = view.findViewById(R.id.profile_country);
//        profileTotalFollowings = view.findViewById(R.id.profile_total_followings);
//        profileTotalFollowers = view.findViewById(R.id.profile_total_followers);
//        editProfileButton = view.findViewById(R.id.profile_edit_button);
//        activitiesLink = view.findViewById(R.id.profile_activities_link);
//        statisticsLink = view.findViewById(R.id.profile_statistics_link);
//        routesLink = view.findViewById(R.id.profile_routes_link);
//        segmentsLink = view.findViewById(R.id.profile_segments_link);
//        bestEffortsLink = view.findViewById(R.id.profile_best_efforts_link);
//        postsLink = view.findViewById(R.id.profile_posts_link);
//        gearLink = view.findViewById(R.id.profile_gear_link);
//        statsContainer = view.findViewById(R.id.profile_stats_fragment_container);
//
//        profileHeaderBackButton.setOnClickListener(v -> {
//            // TODO: Write this if following Strava later
//        });
//        profileSettingButton.setOnClickListener(v -> {
//            navigator.openProfileSettings(this);
//        });
//
//        editProfileButton.setOnClickListener(v -> {
//            navigator.openEditProfile(this);
//        });
//        activitiesLink.setOnClickListener(v -> openComingSoon("Activities"));
//        statisticsLink.setOnClickListener(v -> openComingSoon("Statistics"));
//        routesLink.setOnClickListener(v -> openComingSoon("Routes"));
//        segmentsLink.setOnClickListener(v -> openComingSoon("Segments"));
//        bestEffortsLink.setOnClickListener(v -> openComingSoon("Best Efforts"));
//        gearLink.setOnClickListener(v -> openComingSoon("Gear"));
//        postsLink.setOnClickListener(v -> {
//            navigator.openMyPosts(this);
//        });
//
//        observeOverview();
//        attachStatsFragment();
//        loadProfileData();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (getView() != null) {
//            loadProfileData();
//        }
//    }
//
////    private void loadProfileData() {
////        profileMessage.setVisibility(View.GONE);
////        profileLoginButton.setVisibility(View.GONE);
////        for (int i = 2; i < profileRootContent.getChildCount(); i++) {
////            profileRootContent.getChildAt(i).setVisibility(View.VISIBLE);
////        }
////        viewModel.loadMyOverview();
////        String selectedActivity = viewModel.getSelectedActivityType().getValue();
////        viewModel.loadWeeklySummary(selectedActivity != null
////                ? selectedActivity
////                : ProfileViewModel.ACTIVITY_RUNNING);
////    }
//    private void loadProfileData() {
//
//        profileMessage.setVisibility(View.GONE);
//        profileLoginButton.setVisibility(View.GONE);
//
//        for (int i = 2; i < profileRootContent.getChildCount(); i++) {
//            profileRootContent.getChildAt(i).setVisibility(View.VISIBLE);
//        }
//
//        if (userId == -1) {
//            // 👉 MY PROFILE
//            viewModel.loadMyOverview();
//        } else {
//            // 👉 OTHER USER PROFILE (NEW API cần có)
//            viewModel.loadUserOverview(userId);
//        }
//
//        String selectedActivity = viewModel.getSelectedActivityType().getValue();
//        viewModel.loadWeeklySummary(
//                selectedActivity != null
//                        ? selectedActivity
//                        : ProfileViewModel.ACTIVITY_RUNNING
//        );
//    }
//
//    private void attachStatsFragment() {
//        if (getChildFragmentManager().findFragmentById(R.id.profile_stats_fragment_container) == null) {
//            getChildFragmentManager().beginTransaction()
//                    .replace(R.id.profile_stats_fragment_container, ProfileStatsFragment.newInstance())
//                    .commit();
//        }
//    }
//
//    private void openComingSoon(String title) {
//        navigator.openProfileComingSoon(this, title);
//    }
//
//    private void observeOverview() {
//        viewModel.getProfileOverview().observe(getViewLifecycleOwner(), result -> {
//            if (result instanceof Result.Loading) {
//                profileMessage.setVisibility(View.VISIBLE);
//                profileMessage.setTextColor(com.grouprace.core.system.R.color.text_tertiary);
//                profileMessage.setText("Loading profile overview...");
//            } else if (result instanceof Result.Success) {
//                profileMessage.setVisibility(View.GONE);
//                bindOverview(((Result.Success<ProfileOverview>) result).data);
//            } else if (result instanceof Result.Error) {
//                Result.Error<ProfileOverview> error =
//                        (Result.Error<ProfileOverview>) result;
//                profileMessage.setVisibility(View.VISIBLE);
//                profileMessage.setText(error.message != null
//                        ? error.message
//                        : "Failed to load profile overview.");
//            }
//        });
//    }
//
//    private void bindOverview(ProfileOverview overview) {
//        if (overview == null) {
//            return;
//        }
//
//        ViewHelper.bindOptionalText(profileFullname, overview.getFullname());
//        ViewHelper.bindOptionalText(profileCity, overview.getCity());
//        ViewHelper.bindOptionalText(profileCountry, overview.getCountry());
//        profileTotalFollowings.setText(String.valueOf(overview.getTotalFollowings()));
//        profileTotalFollowers.setText(String.valueOf(overview.getTotalFollowers()));
//        loadAvatarPreview(overview.getAvatarUrl());
//    }
//
//    private void loadAvatarPreview(@Nullable String avatarUrl) {
//        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
//            profileAvatar.setImageDrawable(null);
//            return;
//        }
//
//        new Thread(() -> {
//            try (InputStream inputStream = new URL(avatarUrl).openStream()) {
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                if (bitmap != null && isAdded()) {
//                    requireActivity().runOnUiThread(() -> profileAvatar.setImageBitmap(bitmap));
//                }
//            } catch (IOException ignored) {
//            }
//        }).start();
//    }
//}
package com.grouprace.feature.profile.ui;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.ProfileOverview;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.system.ui.ViewHelper;
import com.grouprace.feature.profile.R;

import javax.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    @Inject
    AppNavigator navigator;

    private ProfileViewModel viewModel;

    // 👉 IMPORTANT: userId for profile (my or other)
    private int userId = -1;

    // Header
    private ImageButton profileHeaderBackButton;
    private ImageButton profileSettingButton;

    private TextView profileMessage;
    private Button profileLoginButton;

    // Overview
    private LinearLayout profileRootContent;
    private TextView profileFullname;
    private ImageView profileAvatar;
    private TextView profileCity;
    private TextView profileCountry;
    private TextView profileTotalFollowings;
    private TextView profileTotalFollowers;
    private Button editProfileButton;
    private View activitiesLink;
    private View statisticsLink;
    private View routesLink;
    private View segmentsLink;
    private View bestEffortsLink;
    private View postsLink;
    private View gearLink;
    private FragmentContainerView statsContainer;

    // =========================
    // NEW INSTANCE (IMPORTANT)
    // =========================
    public static ProfileFragment newInstance(int userId) {
        ProfileFragment fragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putInt("user_id", userId);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // =========================
        // GET ARGUMENTS
        // =========================
        if (getArguments() != null) {
            userId = getArguments().getInt("user_id", -1);
        }

        // Header
        profileHeaderBackButton = view.findViewById(R.id.profile_back_button);
        profileSettingButton = view.findViewById(R.id.profile_setting_button);

        profileMessage = view.findViewById(R.id.profile_message);
        profileLoginButton = view.findViewById(R.id.profile_login_button);

        profileRootContent = view.findViewById(R.id.profile_root_content);
        profileAvatar = view.findViewById(R.id.avatar);
        profileFullname = view.findViewById(R.id.profile_fullname);
        profileCity = view.findViewById(R.id.profile_city);
        profileCountry = view.findViewById(R.id.profile_country);
        profileTotalFollowings = view.findViewById(R.id.profile_total_followings);
        profileTotalFollowers = view.findViewById(R.id.profile_total_followers);
        editProfileButton = view.findViewById(R.id.profile_edit_button);
        activitiesLink = view.findViewById(R.id.profile_activities_link);
        statisticsLink = view.findViewById(R.id.profile_statistics_link);
        routesLink = view.findViewById(R.id.profile_routes_link);
        segmentsLink = view.findViewById(R.id.profile_segments_link);
        bestEffortsLink = view.findViewById(R.id.profile_best_efforts_link);
        postsLink = view.findViewById(R.id.profile_posts_link);
        gearLink = view.findViewById(R.id.profile_gear_link);
        statsContainer = view.findViewById(R.id.profile_stats_fragment_container);

        profileSettingButton.setOnClickListener(v ->
                navigator.openProfileSettings(this));

        editProfileButton.setOnClickListener(v ->
                navigator.openEditProfile(this));

        postsLink.setOnClickListener(v ->
                navigator.openMyPosts(this));

        activitiesLink.setOnClickListener(v -> openComingSoon("Activities"));
        statisticsLink.setOnClickListener(v -> openComingSoon("Statistics"));
        routesLink.setOnClickListener(v -> openComingSoon("Routes"));
        segmentsLink.setOnClickListener(v -> openComingSoon("Segments"));
        bestEffortsLink.setOnClickListener(v -> openComingSoon("Best Efforts"));
        gearLink.setOnClickListener(v -> openComingSoon("Gear"));

        observeOverview();
        attachStatsFragment();
        loadProfileData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            loadProfileData();
        }
    }

    // =========================
    // LOAD PROFILE DATA
    // =========================
    private void loadProfileData() {

        profileMessage.setVisibility(View.GONE);
        profileLoginButton.setVisibility(View.GONE);

        for (int i = 2; i < profileRootContent.getChildCount(); i++) {
            profileRootContent.getChildAt(i).setVisibility(View.VISIBLE);
        }

        if (userId == -1) {
            viewModel.loadMyOverview();
        } else {
//            viewModel.load(userId);
            viewModel.loadMyOverview();
        }

        String selectedActivity = viewModel.getSelectedActivityType().getValue();
        viewModel.loadWeeklySummary(
                selectedActivity != null
                        ? selectedActivity
                        : ProfileViewModel.ACTIVITY_RUNNING
        );
    }

    private void attachStatsFragment() {
        if (getChildFragmentManager().findFragmentById(
                R.id.profile_stats_fragment_container) == null) {

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_stats_fragment_container,
                            ProfileStatsFragment.newInstance())
                    .commit();
        }
    }

    private void openComingSoon(String title) {
        navigator.openProfileComingSoon(this, title);
    }

    private void observeOverview() {
        viewModel.getProfileOverview().observe(getViewLifecycleOwner(), result -> {

            if (result instanceof Result.Loading) {
                profileMessage.setVisibility(View.VISIBLE);
                profileMessage.setText("Loading profile overview...");
            }

            else if (result instanceof Result.Success) {
                profileMessage.setVisibility(View.GONE);

                bindOverview(((Result.Success<ProfileOverview>) result).data);
            }

            else if (result instanceof Result.Error) {
                profileMessage.setVisibility(View.VISIBLE);

                Result.Error<ProfileOverview> error =
                        (Result.Error<ProfileOverview>) result;

                profileMessage.setText(
                        error.message != null
                                ? error.message
                                : "Failed to load profile."
                );
            }
        });
    }

    private void bindOverview(ProfileOverview overview) {

        if (overview == null) return;

        ViewHelper.bindOptionalText(profileFullname, overview.getFullname());
        ViewHelper.bindOptionalText(profileCity, overview.getCity());
        ViewHelper.bindOptionalText(profileCountry, overview.getCountry());

        profileTotalFollowings.setText(String.valueOf(overview.getTotalFollowings()));
        profileTotalFollowers.setText(String.valueOf(overview.getTotalFollowers()));

        loadAvatarPreview(overview.getAvatarUrl());
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
                    requireActivity().runOnUiThread(() ->
                            profileAvatar.setImageBitmap(bitmap));
                }

            } catch (IOException ignored) {}
        }).start();
    }
}