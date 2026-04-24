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
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OverviewFragment extends Fragment {

    private static final String ARG_CLUB_ID = "CLUB_ID";
    @Inject
    AppNavigator appNavigator;
    private OverviewViewModel viewModel;
    private ClubAdminAdapter adapter;

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
        if (clubId == -1) return;

        viewModel = new ViewModelProvider(requireActivity()).get(OverviewViewModel.class);
        viewModel.setClubId(clubId);

        TopAppBarHelper.setupTopAppBar(view, new TopAppBarConfig.Builder()
                .setTitle("Club Overview")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back)
                .setOnLeftIconClick(v -> requireActivity().onBackPressed())
                .build());

        setupViews(view);
        observeViewModel(view);
    }

    private void setupViews(View view) {
        RecyclerView rvAdmins = view.findViewById(R.id.rv_club_admins);
        rvAdmins.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClubAdminAdapter();
        rvAdmins.setAdapter(adapter);

        view.findViewById(R.id.btn_leave_club_action).setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext()).setTitle("Leave Club").setMessage("Are you sure you want to leave this club?").setPositiveButton("Leave", (dialog, which) -> leaveClub()).setNegativeButton("Cancel", null).show();
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
        });

        viewModel.getAdmins().observe(getViewLifecycleOwner(), admins -> {
            if (admins != null) {
                adapter.setAdmins(admins);
            }
        });
    }

    private void leaveClub() {
        viewModel.leaveClub().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                Toast.makeText(getContext(), "You have left the club.", Toast.LENGTH_SHORT).show();
                appNavigator.navigateToClubs(this);
            } else if (result instanceof Result.Error) {
                Toast.makeText(getContext(), "Failed to leave club: " + ((Result.Error<?>) result).message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
