package com.grouprace.feature.profile.ui.follow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.FollowUser;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.profile.R;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FollowListFragment extends Fragment {
    public static final String TAB_FOLLOWERS = "followers";
    public static final String TAB_FOLLOWING = "following";

    private static final String ARG_USER_ID = "arg_user_id";
    private static final String ARG_PROFILE_NAME = "arg_profile_name";
    private static final String ARG_IS_SELF = "arg_is_self";
    private static final String ARG_INITIAL_TAB = "arg_initial_tab";

    @Inject
    AppNavigator navigator;

    private FollowListViewModel viewModel;
    private TextView titleView;
    private Button followersTabButton;
    private Button followingTabButton;
    private ProgressBar progressBar;
    private TextView emptyView;
    private TextView errorView;
    private FollowUserAdapter adapter;
    private int userId;
    private String profileName;
    private boolean isSelf;

    public static FollowListFragment newInstance(int userId, String profileName, boolean isSelf, String initialTab) {
        FollowListFragment fragment = new FollowListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        args.putString(ARG_PROFILE_NAME, profileName);
        args.putBoolean(ARG_IS_SELF, isSelf);
        args.putString(ARG_INITIAL_TAB, initialTab);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_follow_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        userId = args != null ? args.getInt(ARG_USER_ID, -1) : -1;
        profileName = args != null ? args.getString(ARG_PROFILE_NAME) : null;
        isSelf = args != null && args.getBoolean(ARG_IS_SELF, false);
        String initialTab = args != null ? args.getString(ARG_INITIAL_TAB, TAB_FOLLOWERS) : TAB_FOLLOWERS;

        viewModel = new ViewModelProvider(this).get(FollowListViewModel.class);
        viewModel.initialize(userId);

        ImageButton backButton = view.findViewById(R.id.follow_list_back_button);
        titleView = view.findViewById(R.id.follow_list_title);
        followersTabButton = view.findViewById(R.id.followers_tab_button);
        followingTabButton = view.findViewById(R.id.following_tab_button);
        progressBar = view.findViewById(R.id.follow_list_progress);
        emptyView = view.findViewById(R.id.follow_list_empty);
        errorView = view.findViewById(R.id.follow_list_error);
        RecyclerView recyclerView = view.findViewById(R.id.follow_list_recycler);

        titleView.setText(isSelf ? "Connections" : safeProfileName() + "'s Connections");
        backButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        adapter = new FollowUserAdapter(this::openUserProfile);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        followersTabButton.setOnClickListener(v -> viewModel.selectTab(TAB_FOLLOWERS));
        followingTabButton.setOnClickListener(v -> viewModel.selectTab(TAB_FOLLOWING));

        observeSelectedTab();
        observeUsers();
        viewModel.selectTab(initialTab);
    }

    private void observeSelectedTab() {
        viewModel.getSelectedTab().observe(getViewLifecycleOwner(), this::renderSelectedTab);
    }

    private void observeUsers() {
        viewModel.getFollowUsers().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
            } else if (result instanceof Result.Success) {
                progressBar.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                List<FollowUser> users = ((Result.Success<List<FollowUser>>) result).data;
                adapter.submitList(users);
                emptyView.setVisibility((users == null || users.isEmpty()) ? View.VISIBLE : View.GONE);
                if (users == null || users.isEmpty()) {
                    emptyView.setText(getEmptyMessage());
                }
            } else if (result instanceof Result.Error) {
                progressBar.setVisibility(View.GONE);
                adapter.submitList(null);
                emptyView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                String message = ((Result.Error<List<FollowUser>>) result).message;
                errorView.setText(message != null ? message : "Unable to load connections.");
            }
        });
    }

    private void renderSelectedTab(String activeTab) {
        boolean followersSelected = TAB_FOLLOWERS.equals(activeTab);
        styleTabButton(followersTabButton, followersSelected);
        styleTabButton(followingTabButton, !followersSelected);
    }

    private void styleTabButton(Button button, boolean selected) {
        button.setBackgroundResource(selected
                ? com.grouprace.core.system.R.drawable.bg_button_rounded
                : com.grouprace.core.system.R.drawable.bg_button_secondary_rounded);
        button.setBackgroundTintList(null);
        button.setTextColor(requireContext().getColor(android.R.color.black));
    }

    private String getEmptyMessage() {
        String activeTab = viewModel.getSelectedTab().getValue();
        if (TAB_FOLLOWING.equals(activeTab)) {
            return isSelf ? "You are not following anyone yet." : safeProfileName() + " is not following anyone yet.";
        }
        return isSelf ? "You do not have any followers yet." : safeProfileName() + " does not have any followers yet.";
    }

    private String safeProfileName() {
        return profileName != null && !profileName.trim().isEmpty() ? profileName : "This athlete";
    }

    private void openUserProfile(FollowUser user) {
        if (user == null) {
            return;
        }
        navigator.openUserProfile(this, user.getUserId());
    }
}
