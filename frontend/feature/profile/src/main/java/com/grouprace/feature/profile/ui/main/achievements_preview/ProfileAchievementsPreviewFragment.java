package com.grouprace.feature.profile.ui.main.achievements_preview;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.feature.profile.R;
import com.grouprace.feature.profile.ui.main.components.ProfileAchievementsPreviewComponent;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileAchievementsPreviewFragment extends Fragment {
    private static final String ARG_USER_ID = "arg_user_id";
    private static final String ARG_IS_SELF = "arg_is_self";

    @Inject
    AppNavigator navigator;

    private ProfileAchievementsPreviewViewModel viewModel;
    private ProfileAchievementsPreviewComponent component;
    private int userId;
    private boolean self;

    public ProfileAchievementsPreviewFragment() {
        super(R.layout.fragment_profile_achievements_preview);
    }

    public static ProfileAchievementsPreviewFragment newInstance(int userId, boolean isSelf) {
        ProfileAchievementsPreviewFragment fragment = new ProfileAchievementsPreviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        args.putBoolean(ARG_IS_SELF, isSelf);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = getArguments() != null ? getArguments().getInt(ARG_USER_ID, -1) : -1;
        self = getArguments() == null || getArguments().getBoolean(ARG_IS_SELF, true);
        viewModel = new ViewModelProvider(this).get(ProfileAchievementsPreviewViewModel.class);
        viewModel.initialize(userId, self);
        component = new ProfileAchievementsPreviewComponent(view);

        view.findViewById(R.id.profile_achievements_link).setOnClickListener(v ->
                navigator.openProfileAchievements(this, userId, self));

        viewModel.getStatistics().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                component.bind(((Result.Success<RecordProfileStatisticsResponse>) result).data);
            }
        });
        viewModel.loadStatistics();
    }
}
