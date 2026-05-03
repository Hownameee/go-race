package com.grouprace.feature.profile.ui.main.streak;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.model.record.RecordStreakResponse;
import com.grouprace.feature.profile.R;
import com.grouprace.feature.profile.ui.main.components.ProfileStreakComponent;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileStreakFragment extends Fragment {
    private static final String ARG_USER_ID = "arg_user_id";
    private static final String ARG_IS_SELF = "arg_is_self";

    private ProfileStreakViewModel viewModel;
    private ProfileStreakComponent component;
    private boolean self;

    public ProfileStreakFragment() {
        super(R.layout.fragment_profile_streak);
    }

    public static ProfileStreakFragment newInstance(int userId, boolean isSelf) {
        ProfileStreakFragment fragment = new ProfileStreakFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        args.putBoolean(ARG_IS_SELF, isSelf);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int userId = getArguments() != null ? getArguments().getInt(ARG_USER_ID, -1) : -1;
        self = getArguments() == null || getArguments().getBoolean(ARG_IS_SELF, true);
        viewModel = new ViewModelProvider(this).get(ProfileStreakViewModel.class);
        viewModel.initialize(userId, self);
        component = new ProfileStreakComponent(view);

        viewModel.getStreak().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                component.bind(((Result.Success<RecordStreakResponse>) result).data, self);
            }
        });
        viewModel.loadStreak();
    }
}
