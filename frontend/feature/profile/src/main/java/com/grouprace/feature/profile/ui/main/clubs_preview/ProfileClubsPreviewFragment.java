package com.grouprace.feature.profile.ui.main.clubs_preview;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.Club;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.club.ui.adapter.ClubAdapter;
import com.grouprace.feature.profile.R;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileClubsPreviewFragment extends Fragment {
    private static final String ARG_USER_ID = "arg_user_id";
    private static final String ARG_PROFILE_NAME = "arg_profile_name";
    private static final String ARG_IS_SELF = "arg_is_self";

    @Inject
    AppNavigator navigator;

    private ProfileClubsPreviewViewModel viewModel;
    private ClubAdapter adapter;
    private TextView countView;
    private TextView emptyView;
    private RecyclerView recyclerView;
    private int userId;
    private String profileName;
    private boolean self;

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

        userId = getArguments() != null ? getArguments().getInt(ARG_USER_ID, -1) : -1;
        profileName = getArguments() != null ? getArguments().getString(ARG_PROFILE_NAME) : null;
        self = getArguments() == null || getArguments().getBoolean(ARG_IS_SELF, true);

        countView = view.findViewById(R.id.profile_clubs_count);
        emptyView = view.findViewById(R.id.profile_clubs_empty_state);
        recyclerView = view.findViewById(R.id.profile_clubs_preview_recycler);

        adapter = new ClubAdapter();
        adapter.setGridMode(false);
        adapter.setListener(new ClubAdapter.OnClubClickListener() {
            @Override
            public void onClubClick(Club club) {
                navigator.openClubDetail(ProfileClubsPreviewFragment.this, club.getClubId());
            }

            @Override
            public void onJoinClick(Club club) {
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.profile_all_clubs_link).setOnClickListener(v ->
                navigator.openProfileClubs(this, userId, profileName, self));

        viewModel = new ViewModelProvider(this).get(ProfileClubsPreviewViewModel.class);
        viewModel.initialize(self);
        viewModel.getClubs().observe(getViewLifecycleOwner(), this::bindClubs);
        viewModel.sync();
    }

    private void bindClubs(List<Club> clubs) {
        int count = clubs != null ? clubs.size() : 0;
        countView.setText(String.valueOf(count));
        adapter.submitList(clubs);

        boolean empty = clubs == null || clubs.isEmpty();
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        emptyView.setText(self ? "No joined clubs yet." : "Joined clubs are unavailable.");
    }
}
