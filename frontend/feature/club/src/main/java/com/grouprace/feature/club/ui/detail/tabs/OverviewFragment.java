package com.grouprace.feature.club.ui.detail.tabs;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.adapter.ClubAdminAdapter;
import com.grouprace.feature.club.ui.adapter.ClubMemberAdapter;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.core.model.ClubMember;
import com.grouprace.core.model.Club;
import java.util.List;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OverviewFragment extends Fragment {

    private static final String ARG_CLUB_ID = "CLUB_ID";
    @Inject
    AppNavigator appNavigator;
    private OverviewViewModel viewModel;
    private MemberManagementViewModel memberViewModel;
    private ClubMemberAdapter memberAdapter;
    private ClubMemberAdapter pendingAdapter;

    public OverviewFragment() {
        super(R.layout.fragment_club_overview);
    }

    public static OverviewFragment newInstance(int clubId) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLUB_ID, clubId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int clubId = getArguments() != null ? getArguments().getInt(ARG_CLUB_ID, -1) : -1;
        if (clubId == -1)
            return;

        viewModel = new ViewModelProvider(requireActivity()).get(OverviewViewModel.class);
        viewModel.setClubId(clubId);

        memberViewModel = new ViewModelProvider(this).get(MemberManagementViewModel.class);
        memberViewModel.setClubId(clubId);

        TopAppBarHelper.setupTopAppBar(view, new TopAppBarConfig.Builder()
                .setTitle("Club Overview")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back)
                .setOnLeftIconClick(v -> requireActivity().onBackPressed())
                .build());

        setupViews(view);
        observeViewModel(view);
    }

    private void setupViews(View view) {
        RecyclerView rvMembers = view.findViewById(R.id.rv_club_members);
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        memberAdapter = new ClubMemberAdapter(this::onMemberClick);
        rvMembers.setAdapter(memberAdapter);

        RecyclerView rvPending = view.findViewById(R.id.rv_pending_members);
        rvPending.setLayoutManager(new LinearLayoutManager(getContext()));
        pendingAdapter = new ClubMemberAdapter(this::onMemberClick);
        rvPending.setAdapter(pendingAdapter);

        view.findViewById(R.id.btn_leave_club_action).setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Leave Club")
                    .setMessage("Are you sure you want to leave this club?")
                    .setPositiveButton("Leave", (dialog, which) -> leaveClub())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        int clubId = getArguments() != null ? getArguments().getInt(ARG_CLUB_ID, -1) : -1;
        ImageButton btnEdit = view.findViewById(R.id.btn_edit_club);
        btnEdit.setOnClickListener(v -> {
            if (appNavigator != null) {
                appNavigator.openEditClub(this, clubId);
            }
        });
    }

    private void observeViewModel(View view) {
        TextView tvName = view.findViewById(R.id.tv_overview_club_name);
        TextView tvMembers = view.findViewById(R.id.tv_overview_member_count);
        TextView tvPrivacy = view.findViewById(R.id.tv_overview_privacy_badge);
        TextView tvDesc = view.findViewById(R.id.tv_overview_description);
        ImageView ivAvatar = view.findViewById(R.id.iv_overview_avatar);
        ImageButton btnEdit = view.findViewById(R.id.btn_edit_club);

        viewModel.getClub().observe(getViewLifecycleOwner(), club -> {
            if (club != null) {
                tvName.setText(club.getName());
                tvMembers.setText(club.getMemberCount() + " Members");

                if ("private".equalsIgnoreCase(club.getPrivacyType())) {
                    tvPrivacy.setText("Private");
                    tvPrivacy.setTextColor(getResources().getColor(com.grouprace.core.system.R.color.error_red, null));
                    tvPrivacy.setBackgroundResource(R.drawable.bg_badge_private);
                } else {
                    tvPrivacy.setText("Public");
                    tvPrivacy.setTextColor(android.graphics.Color.parseColor("#00BFA5"));
                    tvPrivacy.setBackgroundResource(R.drawable.bg_badge_public);
                }

                tvDesc.setText(club.getDescription());

                if (club.getAvatarUrl() != null && !club.getAvatarUrl().isEmpty()) {
                    Glide.with(this).load(club.getAvatarUrl()).circleCrop().into(ivAvatar);
                }

                boolean isApproved = "approved".equals(club.getStatus());
                view.findViewById(R.id.btn_leave_club_action).setVisibility(isApproved ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getIsLeader().observe(getViewLifecycleOwner(), isLeader -> {
            btnEdit.setVisibility(Boolean.TRUE.equals(isLeader) ? View.VISIBLE : View.GONE);
            updateManagementVisibility(view);
        });

        viewModel.getIsAdmin().observe(getViewLifecycleOwner(), isAdmin -> {
            updateManagementVisibility(view);
        });

        // Member Management Observers
        memberViewModel.getMembers().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                List<ClubMember> all = ((Result.Success<List<ClubMember>>) result).data;

                List<ClubMember> approved = new ArrayList<>();
                List<ClubMember> pending = new ArrayList<>();

                for (ClubMember m : all) {
                    if ("pending".equalsIgnoreCase(m.getStatus())) {
                        pending.add(m);
                    } else {
                        approved.add(m);
                    }
                }

                memberAdapter.setMembers(approved);
                pendingAdapter.setMembers(pending);

                updateManagementVisibility(view);
            }
        });

        memberViewModel.getActionResult().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                Toast.makeText(getContext(), ((Result.Success<String>) result).data, Toast.LENGTH_SHORT).show();
            } else if (result instanceof Result.Error) {
                Toast.makeText(getContext(), "Error: " + ((Result.Error<?>) result).message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateManagementVisibility(View view) {
        boolean isLeader = Boolean.TRUE.equals(viewModel.getIsLeader().getValue());
        boolean isAdmin = Boolean.TRUE.equals(viewModel.getIsAdmin().getValue());
        boolean canManage = isLeader || isAdmin;

        View layoutPending = view.findViewById(R.id.layout_pending_requests);
        // Only show pending section if can manage AND there are pending requests
        boolean hasPending = pendingAdapter != null && pendingAdapter.getItemCount() > 0;
        layoutPending.setVisibility((canManage && hasPending) ? View.VISIBLE : View.GONE);

        // Management title and list visibility
        view.findViewById(R.id.tv_club_management_title).setVisibility(canManage ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.rv_club_members).setVisibility(canManage ? View.VISIBLE : View.GONE);
    }

    private void onMemberClick(ClubMember targetMember) {
        boolean isLeader = Boolean.TRUE.equals(viewModel.getIsLeader().getValue());
        boolean isAdmin = Boolean.TRUE.equals(viewModel.getIsAdmin().getValue());

        if ("pending".equalsIgnoreCase(targetMember.getStatus())) {
            if (isLeader || isAdmin) {
                showJoinRequestDialog(targetMember);
            }
        } else {
            showMemberActionDialog(targetMember, isLeader, isAdmin);
        }
    }

    private void showJoinRequestDialog(ClubMember member) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Join Request")
                .setMessage("Accept " + member.getFullname() + " to the club?")
                .setPositiveButton("Approve",
                        (d, w) -> memberViewModel.updateMemberStatus(member.getUserId(), "approved"))
                .setNegativeButton("Reject",
                        (d, w) -> memberViewModel.updateMemberStatus(member.getUserId(), "rejected"))
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void showMemberActionDialog(ClubMember targetMember, boolean isLeader,
            boolean isAdmin) {
        if (targetMember.isLeader())
            return;

        List<String> options = new ArrayList<>();

        if (isLeader) {
            options.add("admin".equalsIgnoreCase(targetMember.getRole()) ? "Demote from Admin" : "Promote to Admin");
            options.add("Transfer Leadership");
        }

        if (isLeader || (isAdmin && !"admin".equalsIgnoreCase(targetMember.getRole()))) {
            options.add("Kick Member");
        }

        options.add("View Profile");

        if (options.isEmpty())
            return;

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(targetMember.getFullname())
                .setItems(options.toArray(new String[0]), (dialog, which) -> {
                    String selected = options.get(which);
                    if (selected.contains("Admin")) {
                        String newRole = "admin".equalsIgnoreCase(targetMember.getRole()) ? "member" : "admin";
                        memberViewModel.updateMemberRole(targetMember.getUserId(), newRole);
                    } else if (selected.equals("Transfer Leadership")) {
                        showTransferLeadershipDialog(targetMember);
                    } else if (selected.equals("Kick Member")) {
                        memberViewModel.updateMemberStatus(targetMember.getUserId(), "left");
                    } else if (selected.equals("View Profile")) {
                        // TODO: Navigate to profile
                    }
                })
                .show();
    }

    private void showTransferLeadershipDialog(ClubMember member) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Transfer Leadership")
                .setMessage("Are you sure you want to transfer leadership to " + member.getFullname()
                        + "? You will become a regular member.")
                .setPositiveButton("Transfer", (d, w) -> memberViewModel.transferLeadership(member.getUserId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void leaveClub() {
        viewModel.leaveClub().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                Toast.makeText(getContext(), "You have left the club.", Toast.LENGTH_SHORT).show();
                appNavigator.navigateToClubs(this);
            } else if (result instanceof Result.Error) {
                Toast.makeText(getContext(), "Failed to leave club: " + ((Result.Error<?>) result).message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
