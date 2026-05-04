package com.grouprace.feature.profile.ui.routes;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
        ImageButton backButton = view.findViewById(R.id.profile_routes_back_button);
        TextView titleView = view.findViewById(R.id.profile_routes_title);
        TextView messageView = view.findViewById(R.id.profile_routes_message);

        titleView.setText(profileName != null && !profileName.isEmpty() ? profileName + "'s Routes" : "Routes");
        messageView.setText("This user's public routes are not available yet.");
        backButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }
}
