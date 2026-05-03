package com.grouprace.feature.profile.ui.posts;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.model.Post;
import com.grouprace.core.network.utils.SessionManager;
import com.grouprace.feature.profile.R;
import com.grouprace.feature.posts.ui.CommentFragment;
import com.grouprace.feature.posts.ui.ShareActivityFragment;
import com.grouprace.feature.posts.ui.adapter.PostAdapter;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfilePostsFragment extends Fragment {
    private static final String ARG_USER_ID = "arg_user_id";
    private static final String ARG_PROFILE_NAME = "arg_profile_name";
    private static final String ARG_IS_SELF = "arg_is_self";

    @Inject
    AppNavigator navigator;

    @Inject
    SessionManager sessionManager;

    private ProfilePostsViewModel viewModel;
    private PostAdapter adapter;
    private ProgressBar loadingState;
    private TextView errorState;
    private TextView emptyState;
    private RecyclerView recyclerView;

    public ProfilePostsFragment() {
        super(R.layout.fragment_profile_posts);
    }

    public static ProfilePostsFragment newInstance(int userId, @Nullable String profileName, boolean isSelf) {
        ProfilePostsFragment fragment = new ProfilePostsFragment();
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
        boolean isSelf = getArguments() != null && getArguments().getBoolean(ARG_IS_SELF);
        String profileName = getArguments() != null ? getArguments().getString(ARG_PROFILE_NAME) : null;

        ImageButton backButton = view.findViewById(R.id.profile_posts_back_button);
        TextView titleView = view.findViewById(R.id.profile_posts_title);
        loadingState = view.findViewById(R.id.profile_posts_loading_state);
        errorState = view.findViewById(R.id.profile_posts_error_state);
        emptyState = view.findViewById(R.id.profile_posts_empty_state);
        recyclerView = view.findViewById(R.id.profile_posts_recycler_view);

        titleView.setText(isSelf ? "Posts" : (profileName != null && !profileName.isEmpty() ? profileName + "'s Posts" : "Posts"));
        backButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        adapter = new PostAdapter();
        adapter.setOnPostActionListener(new PostAdapter.OnPostActionListener() {
            @Override
            public void onLikeClicked(Post post, int position) {
                if (post.isLiked()) {
                    viewModel.unlikePost(post.getPostId()).observe(getViewLifecycleOwner(), result -> {
                        if (result instanceof Result.Success) {
                            post.setLiked(false);
                            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                            adapter.notifyItemChanged(position, PostAdapter.PAYLOAD_LIKE);
                        }
                    });
                } else {
                    viewModel.likePost(post.getPostId()).observe(getViewLifecycleOwner(), result -> {
                        if (result instanceof Result.Success) {
                            post.setLiked(true);
                            post.setLikeCount(post.getLikeCount() + 1);
                            adapter.notifyItemChanged(position, PostAdapter.PAYLOAD_LIKE);
                        }
                    });
                }
            }

            @Override
            public void onCommentClicked(Post post) {
                CommentFragment.newInstance(post.getPostId())
                        .show(getChildFragmentManager(), "ProfileCommentBottomSheet");
            }

            @Override
            public void onShareClicked(Post post) {
                double distance = post.getDistanceKm() != null ? post.getDistanceKm() : 0.0;
                int seconds = post.getDurationSeconds() != null ? post.getDurationSeconds() : 0;
                String pace;
                if (distance > 0 && seconds > 0) {
                    double paceMinKm = (seconds / 60.0) / distance;
                    int paceMin = (int) paceMinKm;
                    int paceSec = (int) ((paceMinKm - paceMin) * 60);
                    pace = String.format(Locale.getDefault(), "%d:%02d /km", paceMin, paceSec);
                } else {
                    pace = "--:--";
                }

                double speed = post.getSpeed() != null ? post.getSpeed() : 0.0;
                ShareActivityFragment.newInstance(
                        post.getTitle(),
                        String.format(Locale.getDefault(), "%.2f km", distance),
                        pace,
                        TimeUtils.formatDuration(seconds),
                        post.getFullName(),
                        post.getRecordImageUrl(),
                        String.format(Locale.getDefault(), "%.1f km/h", speed)
                ).show(getChildFragmentManager(), "ProfileShareBottomSheet");
            }

            @Override
            public void onReportClicked(Post post) {
                // No-op for now; keep behavior aligned with Home without introducing a new report flow.
            }

            @Override
            public void onPostClicked(Post post) {
                navigator.openPostDetail(ProfilePostsFragment.this, post.getPostId());
            }

            @Override
            public void onOwnerClicked(Post post) {
                if (post.getOwnerId() == sessionManager.getUserId()) {
                    navigator.openMyProfile(ProfilePostsFragment.this);
                    return;
                }
                navigator.openUserProfile(ProfilePostsFragment.this, post.getOwnerId());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ProfilePostsViewModel.class);
        viewModel.initialize(userId, isSelf);
        viewModel.getPosts().observe(getViewLifecycleOwner(), this::displayPosts);
        viewModel.getSyncStatus().observe(getViewLifecycleOwner(), this::bindSyncState);
        viewModel.sync();
    }

    private void bindSyncState(Result<Boolean> result) {
        if (result instanceof Result.Loading) {
            boolean hasCachedPosts = adapter.getItemCount() > 0;
            loadingState.setVisibility(hasCachedPosts ? View.GONE : View.VISIBLE);
            recyclerView.setVisibility(hasCachedPosts ? View.VISIBLE : View.GONE);
            emptyState.setVisibility(View.GONE);
            errorState.setVisibility(View.GONE);
        } else if (result instanceof Result.Success) {
            loadingState.setVisibility(View.GONE);
            errorState.setVisibility(View.GONE);
        } else if (result instanceof Result.Error) {
            loadingState.setVisibility(View.GONE);
            if (adapter.getItemCount() > 0) {
                errorState.setVisibility(View.GONE);
                return;
            }
            errorState.setVisibility(View.VISIBLE);
            String message = ((Result.Error<Boolean>) result).message;
            errorState.setText(message != null ? message : "Failed to load posts.");
        }
    }

    private void displayPosts(List<Post> posts) {
        loadingState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);

        adapter.submitList(posts);

        boolean isEmpty = posts == null || posts.isEmpty();
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }
}
