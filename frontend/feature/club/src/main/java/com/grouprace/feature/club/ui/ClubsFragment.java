package com.grouprace.feature.club.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Club;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.adapter.ClubAdapter;
import com.grouprace.feature.club.ui.detail.ClubDetailFragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ClubsFragment extends Fragment {

    @Inject
    AppNavigator appNavigator;
    private ClubsViewModel viewModel;
    private RecyclerView recyclerView;
    private ClubAdapter clubAdapter;

    public ClubsFragment() {
        super(R.layout.fragment_clubs);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ClubsViewModel.class);

        TopAppBarHelper.setupTopAppBar(view, getTopAppBarConfig());

        recyclerView = view.findViewById(R.id.recycler_view_clubs);

        clubAdapter = new ClubAdapter();
        recyclerView.setAdapter(clubAdapter);

        // Standard navigation
        clubAdapter.setListener(new ClubAdapter.OnClubClickListener() {
            @Override
            public void onClubClick(Club club) {
                // Navigate to detail
                Bundle bundle = new Bundle();
                bundle.putString("CLUB_ID", ((Integer) (club.getClubId())).toString());
                ClubDetailFragment detailFragment = new ClubDetailFragment();
                detailFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction().replace(getId(), detailFragment) // fallback, usually handled nicely via navigation component
                        .addToBackStack(null).commit();
            }

            @Override
            public void onJoinClick(Club club) {
                viewModel.joinClub(String.valueOf(club.getClubId())).observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Error) {
                        Toast.makeText(getContext(), "Failed to join club", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button btnCreateClub = view.findViewById(R.id.button_create_club_action);
        btnCreateClub.setOnClickListener(v -> {
            if (appNavigator != null) {
                appNavigator.navigateToCreateClub(this);
            }
        });

        observeViewModel();
        setupScrollListener();
    }

    private void observeViewModel() {
        viewModel.getIsDiscoverMode().observe(getViewLifecycleOwner(), isDiscover -> {
            if (isDiscover) {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                clubAdapter.setGridMode(true);
            } else {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                clubAdapter.setGridMode(false);
            }
            clubAdapter.notifyDataSetChanged();
        });

        viewModel.getClubs().observe(getViewLifecycleOwner(), clubs -> {
            clubAdapter.submitList(clubs);
        });

        viewModel.getSyncStatus().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                String type = ((Result.Success<String>) result).data;
                boolean isDiscover = "discover clubs".equals(type);
                viewModel.setDiscoverMode(isDiscover);
            } else if (result instanceof Result.Error) {
                if (clubAdapter.getItemCount() == 0) {
                    viewModel.setDiscoverMode(true);
                }
            }
        });
    }

    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = -1;

                    if (layoutManager instanceof LinearLayoutManager) {
                        firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    } else if (layoutManager instanceof GridLayoutManager) {
                        firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    }

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        viewModel.loadMore(clubAdapter.getItemCount());
                    }
                }
            }
        });
    }

    private TopAppBarConfig getTopAppBarConfig() {
        return new TopAppBarConfig.Builder().setTitle("Clubs").setLeftIcon(com.grouprace.core.system.R.drawable.ic_app).addRightIcon(com.grouprace.core.system.R.drawable.ic_search, v -> {
            if (appNavigator != null) {
                appNavigator.navigateToSearch(this);
            }
        }).addRightIcon(com.grouprace.core.system.R.drawable.ic_notification, v -> {
            if (appNavigator != null) {
                appNavigator.navigateToNotification(this);
            }
        }).build();
    }
}
