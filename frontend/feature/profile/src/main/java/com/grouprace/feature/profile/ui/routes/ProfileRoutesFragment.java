package com.grouprace.feature.profile.ui.routes;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.profile.R;

public class ProfileRoutesFragment extends Fragment {
    private static final String ARG_PROFILE_NAME = "arg_profile_name";

    public ProfileRoutesFragment() {
        super(R.layout.fragment_profile_routes);
    }

    public static ProfileRoutesFragment newInstance(@Nullable String profileName) {
        ProfileRoutesFragment fragment = new ProfileRoutesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROFILE_NAME, profileName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String profileName = getArguments() != null ? getArguments().getString(ARG_PROFILE_NAME) : null;
        TextView messageView = view.findViewById(R.id.profile_routes_message);

        setupTopBar(view, profileName);
        messageView.setText("This user's public routes are not available yet.");
    }

    private void setupTopBar(View view, @Nullable String profileName) {
        String title = profileName != null && !profileName.isEmpty() ? profileName + "'s Routes" : "Routes";
        TopAppBarHelper.setupTopAppBar(view, new TopAppBarConfig.Builder()
                .setTitle(title)
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back, v -> requireActivity().onBackPressed())
                .build());
    }
}
