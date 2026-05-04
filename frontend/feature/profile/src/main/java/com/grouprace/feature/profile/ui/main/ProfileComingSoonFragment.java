package com.grouprace.feature.profile.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.profile.R;

public class ProfileComingSoonFragment extends Fragment {
    private static final String ARG_TITLE = "arg_title";

    public static ProfileComingSoonFragment newInstance(String title) {
        ProfileComingSoonFragment fragment = new ProfileComingSoonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_coming_soon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView titleView = view.findViewById(R.id.coming_soon_title);

        String title = getArguments() != null ? getArguments().getString(ARG_TITLE) : null;
        if (title != null && !title.trim().isEmpty()) {
            titleView.setText(title);
        }

        setupTopBar(view, title);
    }

    private void setupTopBar(View view, @Nullable String title) {
        TopAppBarHelper.setupTopAppBar(view, new TopAppBarConfig.Builder()
                .setTitle(title != null ? title : "Coming Soon")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back, v -> requireActivity().onBackPressed())
                .build());
    }
}
