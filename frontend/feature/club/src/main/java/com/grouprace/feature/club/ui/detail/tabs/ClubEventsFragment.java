package com.grouprace.feature.club.ui.detail.tabs;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.result.Result;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.adapter.EventAdapter;
import com.grouprace.core.model.ClubEvent;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ClubEventsFragment extends Fragment {
    @Inject
    AppNavigator appNavigator;

    private static final String ARG_CLUB_ID = "CLUB_ID";
    private ClubEventsViewModel viewModel;
    private EventAdapter adapter;
    private ProgressBar pbLoading;
    private View layoutEmptyState;
    private TabLayout tabFilter;
    private List<ClubEvent> allEvents = new ArrayList<>();
    private int clubId;

    public ClubEventsFragment() {
        super(R.layout.fragment_club_events);
    }

    public static ClubEventsFragment newInstance(int clubId) {
        ClubEventsFragment fragment = new ClubEventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLUB_ID, clubId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clubId = getArguments() != null ? getArguments().getInt(ARG_CLUB_ID, -1) : -1;
        if (clubId == -1) return;

        viewModel = new ViewModelProvider(this).get(ClubEventsViewModel.class);
        
        setupTopBar(view, false); // Default, will update when leader status is known
        
        viewModel.checkIsLeader(clubId).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                boolean isLeader = ((Result.Success<Boolean>) result).data;
                setupTopBar(view, isLeader);
            }
        });

        pbLoading = view.findViewById(R.id.pb_loading);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        tabFilter = view.findViewById(R.id.tab_event_filter);
        
        RecyclerView rvEvents = view.findViewById(R.id.rv_events);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new EventAdapter(new EventAdapter.OnEventClickListener() {
            @Override
            public void onJoinClick(ClubEvent event) {
                viewModel.joinEvent(clubId, event.getEventId()).observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Success) {
                        Toast.makeText(getContext(), "Joined successfully", Toast.LENGTH_SHORT).show();
                        viewModel.syncEvents(clubId);
                    } else if (result instanceof Result.Error) {
                        Toast.makeText(getContext(), ((Result.Error<?>) result).message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onEventClick(ClubEvent event) {
                appNavigator.openEventDetail(ClubEventsFragment.this, clubId, event.getEventId());
            }
        });
        
        rvEvents.setAdapter(adapter);

        tabFilter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterEvents();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 1. Trigger background sync
        pbLoading.setVisibility(View.VISIBLE);
        viewModel.syncEvents(clubId).observe(getViewLifecycleOwner(), result -> {
            pbLoading.setVisibility(View.GONE);
            if (result instanceof Result.Error) {
                Toast.makeText(getContext(), "Failed to sync events", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Observe local data
        viewModel.getLocalEvents(clubId).observe(getViewLifecycleOwner(), events -> {
            this.allEvents = events != null ? events : new ArrayList<>();
            filterEvents();
        });
    }

    private void setupTopBar(View view, boolean isLeader) {
        TopAppBarConfig.Builder builder = new TopAppBarConfig.Builder()
                .setTitle("Events")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back)
                .setOnLeftIconClick(v -> requireActivity().onBackPressed());

        if (isLeader) {
            builder.addRightIcon(com.grouprace.core.system.R.drawable.ic_add, v -> {
                appNavigator.openCreateEvent(this, clubId);
            });
        }

        TopAppBarHelper.setupTopAppBar(view, builder.build());
    }

    private void filterEvents() {
        boolean isUpcoming = tabFilter.getSelectedTabPosition() == 0;
        long now = System.currentTimeMillis();
        
        List<ClubEvent> filtered = allEvents.stream()
                .filter(e -> {
                    try {
                        java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US);
                        isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                        java.util.Date endDate = isoFormat.parse(e.getEndTime());
                        if (endDate == null) return isUpcoming;
                        return isUpcoming ? endDate.getTime() >= now : endDate.getTime() < now;
                    } catch (Exception ex) {
                        return isUpcoming;
                    }
                })
                .collect(Collectors.toList());
        
        adapter.submitList(filtered);
        layoutEmptyState.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
