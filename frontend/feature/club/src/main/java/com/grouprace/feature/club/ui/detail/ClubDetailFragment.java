package com.grouprace.feature.club.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Post;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.ShareClubFragment;
import com.grouprace.feature.club.ui.adapter.ClubNavAdapter;
import com.grouprace.feature.posts.ui.CommentFragment;
import com.grouprace.feature.posts.ui.ShareActivityFragment;
import com.grouprace.feature.posts.ui.adapter.PostAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ClubDetailFragment extends Fragment {
    @Inject
    AppNavigator appNavigator;
    private ClubDetailViewModel viewModel;
    private android.widget.Button btnJoinClub;
    private LinearLayout layoutMemberActions;
    private ImageView ivAvatar;
    private RecyclerView rvPosts;
    private PostAdapter postAdapter;
    private LinearLayout layoutPrivatePlaceholder;
    private View progressBar;
    private View tvError;
    private android.widget.Button btnInvite;
    private android.widget.Button btnShare;
    private boolean isLoadingPage = false;

    public ClubDetailFragment() {
        super(R.layout.fragment_club_detail);
    }

    public static ClubDetailFragment newInstance(int clubId) {
        ClubDetailFragment fragment = new ClubDetailFragment();
        Bundle args = new Bundle();
        args.putInt("CLUB_ID", clubId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ClubDetailViewModel.class);
        TopAppBarHelper.setupTopAppBar(view, getTopAppBarConfig());

        int clubId = getArguments() != null ? getArguments().getInt("CLUB_ID", -1) : -1;
        if (clubId != -1) {
            viewModel.loadClub(clubId);
        }

        setupViews(view);
        observeViewModel(view);
    }

    private void setupViews(View view) {
        btnJoinClub = view.findViewById(R.id.btn_join_club);
        layoutMemberActions = view.findViewById(R.id.layout_member_actions);
        ivAvatar = view.findViewById(R.id.iv_club_avatar);
        rvPosts = view.findViewById(R.id.rv_club_posts);
        layoutPrivatePlaceholder = view.findViewById(R.id.layout_private_club_placeholder);
        progressBar = view.findViewById(R.id.loading_state);
        tvError = view.findViewById(R.id.error_state);
        btnInvite = view.findViewById(R.id.btn_invite);
        btnShare = view.findViewById(R.id.btn_share);

        btnJoinClub.setOnClickListener(v -> {
            viewModel.joinClub();
            Toast.makeText(getContext(), "Joining Club...", Toast.LENGTH_SHORT).show();
        });

        View.OnClickListener shareListener = v -> {
            com.grouprace.core.model.Club club = viewModel.getClub().getValue();
            if (club != null) {
                ShareClubFragment.newInstance(club.getName(), club.getMemberCount(), club.getAvatarUrl()).show(getChildFragmentManager(), "ShareClubBottomSheet");
            }
        };
        btnInvite.setOnClickListener(shareListener);
        btnShare.setOnClickListener(shareListener);

        setupNavList(view);
        setupPostList();
    }

    private void setupNavList(View view) {
        RecyclerView rvNav = view.findViewById(R.id.rv_nav_buttons);
        List<ClubNavAdapter.NavItem> navItems = new ArrayList<>();

        ClubNavAdapter adapter = new ClubNavAdapter(navItems, item -> {
            switch (item.getId()) {
                case "ACTION_LEAVE":
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext()).setTitle("Leave Club")
                            .setMessage("Are you sure you want to leave this club?")
                            .setPositiveButton("Leave", (dialog, which) -> {
                                viewModel.leaveClub();
                                appNavigator.navigateToClubs(this);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    break;
                case "ACTION_CREATE_POST":
                    if (viewModel.getClub().getValue() != null) {
                        Log.d("dosthhhh", "Club ID: " + viewModel.getClub().getValue().getClubId());
                        appNavigator.openAddPost(this, false, viewModel.getClub().getValue().getClubId());
                    }
                    break;
                case "ACTION_CREATE_ACTIVITY":
                    if (viewModel.getClub().getValue() != null) {
                        appNavigator.openAddPost(this, true, viewModel.getClub().getValue().getClubId());
                    }
                    break;
                case "NAV_OVERVIEW":
                    if (viewModel.getClub().getValue() != null) {
                        appNavigator.openClubOverview(this, viewModel.getClub().getValue().getClubId());
                    }
                    break;
                case "NAV_EVENTS":
                    if (viewModel.getClub().getValue() != null) {
                        appNavigator.openClubEvents(this, viewModel.getClub().getValue().getClubId());
                    }
                    break;
                case "NAV_STATS":
                    if (viewModel.getClub().getValue() != null) {
                        appNavigator.openClubStats(this, viewModel.getClub().getValue().getClubId());
                    }
                    break;
                default:
                    break;
            }
        });

        rvNav.setAdapter(adapter);
    }

    private void setupPostList() {
        postAdapter = new PostAdapter();
        postAdapter.setOnPostActionListener(new PostAdapter.OnPostActionListener() {
            @Override
            public void onLikeClicked(Post post, int position) {

                if (post.isLiked()) {
                    viewModel.unlikePost(post.getPostId()).observe(getViewLifecycleOwner(), result -> {
                        if (result instanceof Result.Success) {
                            post.setLiked(false);
                            post.setLikeCount(post.getLikeCount() - 1);
                            postAdapter.notifyItemChanged(position);
                        }
                    });
                } else {
                    viewModel.likePost(post.getPostId()).observe(getViewLifecycleOwner(), result -> {
                        if (result instanceof Result.Success) {
                            post.setLiked(true);
                            post.setLikeCount(post.getLikeCount() + 1);
                            postAdapter.notifyItemChanged(position);
                        }
                    });
                }
            }

            @Override
            public void onCommentClicked(Post post) {
                CommentFragment.newInstance(post.getPostId()).show(getChildFragmentManager(), "CommentBottomSheet");
            }

            @Override
            public void onReportClicked(Post post) {
                // TODO: implement report
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

                double speedVal = post.getSpeed() != null ? post.getSpeed() : 0.0;
                String speedStr = String.format(Locale.getDefault(), "%.1f km/h", speedVal);

                ShareActivityFragment.newInstance(post.getTitle(), String.format(Locale.getDefault(), "%.2f km", distance), pace, TimeUtils.formatDuration(seconds), post.getFullName(), post.getRecordImageUrl(), speedStr).show(getChildFragmentManager(), "ShareBottomSheet");
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);
        rvPosts.setAdapter(postAdapter);

        setupInfiniteScroll(layoutManager);
    }

    private void setupInfiniteScroll(LinearLayoutManager layoutManager) {
        rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && !isLoadingPage) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        isLoadingPage = true;
                        String cursor = postAdapter.getLastPostCreatedAt();
                        if (cursor != null) {
                            viewModel.fetchPosts(cursor);
                        }
                    }
                }
            }
        });
    }

    private void observeViewModel(View view) {
        TextView tvClubName = view.findViewById(R.id.tv_club_name);
        TextView tvMemberCount = view.findViewById(R.id.tv_member_count);
        TextView tvPrivacyBadge = view.findViewById(R.id.tv_privacy_badge);

        viewModel.getClub().observe(getViewLifecycleOwner(), club -> {
            if (club != null) {
                tvClubName.setText(club.getName());
                tvMemberCount.setText(club.getMemberCount() + " Members");

                boolean isPrivate = "private".equalsIgnoreCase(club.getPrivacyType());
                String status = club.getStatus();
                boolean isApproved = "approved".equals(status);

                if (isPrivate) {
                    tvPrivacyBadge.setText("Private");
                    tvPrivacyBadge.setTextColor(getResources().getColor(com.grouprace.core.system.R.color.error_red, null));
                    tvPrivacyBadge.setBackgroundResource(R.drawable.bg_badge_private);
                } else {
                    tvPrivacyBadge.setText("Public");
                    tvPrivacyBadge.setTextColor(android.graphics.Color.parseColor("#00BFA5"));
                    tvPrivacyBadge.setBackgroundResource(R.drawable.bg_badge_public);
                }

                if (club.getAvatarUrl() != null && !club.getAvatarUrl().isEmpty()) {
                    com.bumptech.glide.Glide.with(this).load(club.getAvatarUrl()).circleCrop().into(ivAvatar);
                }

                // Handle Newsfeed visibility
                if (isPrivate && !isApproved) {
                    layoutPrivatePlaceholder.setVisibility(View.VISIBLE);
                    rvPosts.setVisibility(View.GONE);
                } else {
                    layoutPrivatePlaceholder.setVisibility(View.GONE);
                    rvPosts.setVisibility(View.VISIBLE);
                }

                // Update Nav Bar
                RecyclerView rvNav = view.findViewById(R.id.rv_nav_buttons);
                if (rvNav.getAdapter() instanceof ClubNavAdapter) {
                    ClubNavAdapter adapter = (ClubNavAdapter) rvNav.getAdapter();
                    List<ClubNavAdapter.NavItem> items = new ArrayList<>();
                    items.add(new ClubNavAdapter.NavItem("NAV_OVERVIEW", "Overview", android.R.drawable.ic_dialog_info));

                    if (isApproved) {
                        items.add(new ClubNavAdapter.NavItem("ACTION_CREATE_POST", "Create Post", android.R.drawable.ic_menu_edit));
                        items.add(new ClubNavAdapter.NavItem("ACTION_CREATE_ACTIVITY", "Create Activity", android.R.drawable.ic_menu_add));
                    }

                    items.add(new ClubNavAdapter.NavItem("NAV_EVENTS", "Events", android.R.drawable.ic_menu_my_calendar));
                    items.add(new ClubNavAdapter.NavItem("NAV_STATS", "Statistics", android.R.drawable.ic_menu_gallery));
                    
                    if (isApproved) {
                        items.add(new ClubNavAdapter.NavItem("ACTION_LEAVE", "Leave Club", android.R.drawable.ic_delete));
                    }

                    adapter.updateItems(items);
                }

                // Handle Join Button
                if (isApproved) {
                    btnJoinClub.setVisibility(View.GONE);
                    layoutMemberActions.setVisibility(View.VISIBLE);
                } else if ("pending".equals(status)) {
                    btnJoinClub.setVisibility(View.VISIBLE);
                    btnJoinClub.setText("Pending Approval");
                    btnJoinClub.setEnabled(false);
                    layoutMemberActions.setVisibility(View.GONE);
                } else {
                    btnJoinClub.setVisibility(View.VISIBLE);
                    btnJoinClub.setText("Join Club");
                    btnJoinClub.setEnabled(true);
                    layoutMemberActions.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getClubPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                postAdapter.submitList(posts);
                if (!posts.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    rvPosts.setVisibility(View.VISIBLE);
                    tvError.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getSyncStatus().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result instanceof Result.Loading) {
                if (postAdapter.getItemCount() == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    rvPosts.setVisibility(View.GONE);
                }
                tvError.setVisibility(View.GONE);
                isLoadingPage = true;

            } else if (result instanceof Result.Success) {
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
                rvPosts.setVisibility(View.VISIBLE);
                isLoadingPage = false;

            } else if (result instanceof Result.Error) {
                progressBar.setVisibility(View.GONE);
                isLoadingPage = false;

                if (postAdapter.getItemCount() == 0) {
                    rvPosts.setVisibility(View.GONE);
                    tvError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private TopAppBarConfig getTopAppBarConfig() {
        return new TopAppBarConfig.Builder().setTitle("Club Details").setLeftIcon(com.grouprace.core.system.R.drawable.ic_app).build();
    }
}