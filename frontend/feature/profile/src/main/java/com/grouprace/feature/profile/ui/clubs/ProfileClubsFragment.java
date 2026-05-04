package com.grouprace.feature.profile.ui.clubs;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Club;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.club.ui.adapter.ClubAdapter;
import com.grouprace.feature.profile.R;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileClubsFragment extends Fragment {
    private static final String ARG_USER_ID = "arg_user_id";
    private static final String ARG_PROFILE_NAME = "arg_profile_name";
    private static final String ARG_IS_SELF = "arg_is_self";

    @Inject
    AppNavigator navigator;

    private ProfileClubsViewModel viewModel;
    private ClubAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private TextView errorView;
    private View loadingView;
    private boolean self;

    public ProfileClubsFragment() {
        super(R.layout.fragment_profile_clubs);
    }

    public static ProfileClubsFragment newInstance(int userId, @Nullable String profileName, boolean isSelf) {
        ProfileClubsFragment fragment = new ProfileClubsFragment();
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

        self = getArguments() == null || getArguments().getBoolean(ARG_IS_SELF, true);
        String profileName = getArguments() != null ? getArguments().getString(ARG_PROFILE_NAME) : null;

        ImageButton backButton = view.findViewById(R.id.profile_clubs_back_button);
        TextView titleView = view.findViewById(R.id.profile_clubs_title);
        recyclerView = view.findViewById(R.id.profile_clubs_recycler_view);
        emptyView = view.findViewById(R.id.profile_clubs_empty_state);
        errorView = view.findViewById(R.id.profile_clubs_error_state);
        loadingView = view.findViewById(R.id.profile_clubs_loading_state);

        titleView.setText(self
                ? "Clubs"
                : (profileName != null && !profileName.isEmpty() ? profileName + "'s Clubs" : "Clubs"));
        backButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        adapter = new ClubAdapter();
        adapter.setGridMode(false);
        adapter.setListener(new ClubAdapter.OnClubClickListener() {
            @Override
            public void onClubClick(Club club) {
                navigator.openClubDetail(ProfileClubsFragment.this, club.getClubId());
            }

            @Override
            public void onJoinClick(Club club) {
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ProfileClubsViewModel.class);
        viewModel.initialize(self);
        viewModel.getClubs().observe(getViewLifecycleOwner(), this::bindClubs);
        viewModel.getSyncStatus().observe(getViewLifecycleOwner(), this::bindSyncState);
        viewModel.sync();
    }

    private void bindClubs(List<Club> clubs) {
        adapter.submitList(clubs);
        boolean isEmpty = clubs == null || clubs.isEmpty();
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        emptyView.setText(self ? "No joined clubs yet." : "Joined clubs are unavailable.");

        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && adapter.getItemCount() > 0) {
                    viewModel.loadMore(adapter.getItemCount());
                }
            }
        });
    }

    private void bindSyncState(Result<String> result) {
        if (result instanceof Result.Loading) {
            loadingView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            errorView.setVisibility(View.GONE);
        } else if (result instanceof Result.Error) {
            loadingView.setVisibility(View.GONE);
            if (adapter.getItemCount() == 0) {
                errorView.setVisibility(View.VISIBLE);
                errorView.setText(((Result.Error<String>) result).message);
            }
        } else {
            loadingView.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
        }
    }
}
