package com.grouprace.feature.profile.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        ImageButton backButton = view.findViewById(R.id.coming_soon_back_button);
        TextView titleView = view.findViewById(R.id.coming_soon_title);

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        String title = getArguments() != null ? getArguments().getString(ARG_TITLE) : null;
        if (title != null && !title.trim().isEmpty()) {
            titleView.setText(title);
        }
    }
}
