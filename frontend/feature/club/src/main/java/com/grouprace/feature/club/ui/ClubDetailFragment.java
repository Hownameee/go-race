package com.grouprace.feature.club.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.adapter.ClubNavAdapter;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ClubDetailFragment extends Fragment {
    private ClubDetailViewModel viewModel;

    private Button btnJoinClub;
    private LinearLayout layoutMemberActions;
    private ImageView ivAvatar;

    public ClubDetailFragment() {
        super(R.layout.fragment_club_detail);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ClubDetailViewModel.class);
        TopAppBarHelper.setupTopAppBar(view, getTopAppBarConfig());

        String clubId = getArguments() != null ? getArguments().getString("CLUB_ID", "") : "";

        viewModel.loadClub(clubId.isEmpty() ? 1 : Integer.parseInt(clubId));

        setupViews(view);
        observeViewModel(view);
    }

    private void setupViews(View view) {
        btnJoinClub = view.findViewById(R.id.btn_join_club);
        layoutMemberActions = view.findViewById(R.id.layout_member_actions);
        ivAvatar = view.findViewById(R.id.iv_club_avatar);

        btnJoinClub.setOnClickListener(v -> {
            viewModel.joinClub();
            Toast.makeText(getContext(), "Joining Club...", Toast.LENGTH_SHORT).show();
        });

        RecyclerView rvNav = view.findViewById(R.id.rv_nav_buttons);

        List<ClubNavAdapter.NavItem> navItems = new ArrayList<>();

        ClubNavAdapter adapter = new ClubNavAdapter(navItems, item -> {
            if ("ACTION_LEAVE".equals(item.getId())) {
                viewModel.leaveClub();
                navigateTo(this, item.getTargetFragment());
            } else {
                navigateTo(this, item.getTargetFragment());
            }
        });

        rvNav.setAdapter(adapter);
    }

    private void observeViewModel(View view) {
        TextView tvClubName = view.findViewById(R.id.tv_club_name);
        TextView tvMemberCount = view.findViewById(R.id.tv_member_count);
        TextView tvPrivacyBadge = view.findViewById(R.id.tv_privacy_badge);

        viewModel.getClub().observe(getViewLifecycleOwner(), club -> {
            if (club != null) {
                tvClubName.setText(club.getName());
                tvMemberCount.setText(club.getMemberCount() + " Members");
                if ("private".equalsIgnoreCase(club.getPrivacyType())) {
                    tvPrivacyBadge.setText("Private");
                    tvPrivacyBadge.setTextColor(getResources().getColor(com.grouprace.core.system.R.color.error_red, null));
                    tvPrivacyBadge.setBackgroundResource(R.drawable.bg_badge_private);
                } else {
                    tvPrivacyBadge.setText("Public");
                    tvPrivacyBadge.setTextColor(android.graphics.Color.parseColor("#00BFA5"));
                    tvPrivacyBadge.setBackgroundResource(R.drawable.bg_badge_public);
                }

                if (club.getAvatarUrl() != null && !club.getAvatarUrl().isEmpty()) {
                    com.bumptech.glide.Glide.with(this)
                        .load(club.getAvatarUrl())
                        .circleCrop()
                        .into(ivAvatar);
                }

                String status = club.getStatus();

                RecyclerView rvNav = view.findViewById(R.id.rv_nav_buttons);
                if (rvNav.getAdapter() instanceof ClubNavAdapter) {
                    ClubNavAdapter adapter = (ClubNavAdapter) rvNav.getAdapter();
                    List<ClubNavAdapter.NavItem> items = new ArrayList<>();
                    items.add(new ClubNavAdapter.NavItem("NAV_OVERVIEW", "Overview", android.R.drawable.ic_dialog_info, new Fragment()));
                    items.add(new ClubNavAdapter.NavItem("NAV_EVENTS", "Events", android.R.drawable.ic_menu_my_calendar, new Fragment()));
                    items.add(new ClubNavAdapter.NavItem("NAV_ACTIVITIES", "Activities", android.R.drawable.ic_menu_sort_by_size, new Fragment()));
                    items.add(new ClubNavAdapter.NavItem("NAV_STATS", "Statistics", android.R.drawable.ic_menu_gallery, new Fragment()));
                    
                    if ("approved".equals(status)) {
                        items.add(new ClubNavAdapter.NavItem("ACTION_LEAVE", "Leave Club", android.R.drawable.ic_delete, new ClubsFragment()));
                    }
                    adapter.updateItems(items);
                }

                if ("approved".equals(status)) {
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
    }

    private void navigateTo(Fragment currentFragment, Fragment targetFragment) {
        if (targetFragment == null) return;
        if (currentFragment != null && currentFragment.getView() != null && currentFragment.getView().getParent() != null) {
            int containerId = ((ViewGroup) currentFragment.getView().getParent()).getId();
            currentFragment.requireActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out).replace(containerId, targetFragment).addToBackStack(null).commit();
        }
    }

    private TopAppBarConfig getTopAppBarConfig() {
        return new TopAppBarConfig.Builder().setTitle("Club Details").setLeftIcon(com.grouprace.core.system.R.drawable.ic_app).build();
    }
}