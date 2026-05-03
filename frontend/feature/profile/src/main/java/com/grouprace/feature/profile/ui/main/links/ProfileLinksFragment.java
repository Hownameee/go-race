package com.grouprace.feature.profile.ui.main.links;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.profile.R;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileLinksFragment extends Fragment {
    private static final String ARG_USER_ID = "arg_user_id";
    private static final String ARG_PROFILE_NAME = "arg_profile_name";
    private static final String ARG_IS_SELF = "arg_is_self";

    @Inject
    AppNavigator navigator;

    private int userId;
    private String profileName;
    private boolean self;
    private ProfileLinksAdapter adapter;

    public ProfileLinksFragment() {
        super(R.layout.fragment_profile_links);
    }

    public static ProfileLinksFragment newInstance(int userId, @Nullable String profileName, boolean isSelf) {
        ProfileLinksFragment fragment = new ProfileLinksFragment();
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

        adapter = new ProfileLinksAdapter(this::handleLinkClick);
        RecyclerView recyclerView = view.findViewById(R.id.profile_links_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

        adapter.submitList(buildLinks());
    }

    private List<ProfileLinkItem> buildLinks() {
        return Arrays.asList(
                new ProfileLinkItem(
                        ProfileLinkItem.Action.ACTIVITIES,
                        R.drawable.ic_profile_activities,
                        "Activities",
                        "See all activities"
                ),
                new ProfileLinkItem(
                        ProfileLinkItem.Action.STATISTICS,
                        R.drawable.ic_profile_statistics,
                        "Statistics",
                        "View detailed statistics"
                ),
                new ProfileLinkItem(
                        ProfileLinkItem.Action.ROUTES,
                        R.drawable.ic_profile_routes,
                        "Routes",
                        null
                ),
                new ProfileLinkItem(
                        ProfileLinkItem.Action.POSTS,
                        R.drawable.ic_profile_posts,
                        "Posts",
                        null
                ),
                new ProfileLinkItem(
                        ProfileLinkItem.Action.CLUBS,
                        com.grouprace.core.system.R.drawable.ic_nav_clubs,
                        "Clubs",
                        null
                )
        );
    }

    private void handleLinkClick(ProfileLinkItem item) {
        switch (item.getAction()) {
            case ACTIVITIES:
                navigator.openProfileActivities(this, userId, profileName, self);
                break;
            case STATISTICS:
                navigator.openProfileStatistics(this, userId, self);
                break;
            case ROUTES:
                navigator.openProfileRoutes(this, userId, profileName, self);
                break;
            case POSTS:
                navigator.openProfilePosts(this, userId, profileName, self);
                break;
            case CLUBS:
                navigator.openProfileClubs(this, userId, profileName, self);
                break;
        }
    }
}
