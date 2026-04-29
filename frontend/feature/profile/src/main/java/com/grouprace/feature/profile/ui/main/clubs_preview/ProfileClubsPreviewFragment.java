package com.grouprace.feature.profile.ui.main.clubs_preview;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.profile.R;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileClubsPreviewFragment extends Fragment {
    private static final String ARG_USER_ID = "arg_user_id";
    private static final String ARG_PROFILE_NAME = "arg_profile_name";
    private static final String ARG_IS_SELF = "arg_is_self";

    @Inject
    AppNavigator navigator;

    public ProfileClubsPreviewFragment() {
        super(R.layout.fragment_profile_clubs_preview);
    }

    public static ProfileClubsPreviewFragment newInstance(int userId, @Nullable String profileName, boolean isSelf) {
        ProfileClubsPreviewFragment fragment = new ProfileClubsPreviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        args.putString(ARG_PROFILE_NAME, profileName);
        args.putBoolean(ARG_IS_SELF, isSelf);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int userId = getArguments() != null ? getArguments().getInt(ARG_USER_ID, -1) : -1;
        String profileName = getArguments() != null ? getArguments().getString(ARG_PROFILE_NAME) : null;
        boolean self = getArguments() == null || getArguments().getBoolean(ARG_IS_SELF, true);
        view.findViewById(R.id.profile_all_clubs_link).setOnClickListener(v ->
                navigator.openProfileClubs(this, userId, profileName, self));
    }
}
