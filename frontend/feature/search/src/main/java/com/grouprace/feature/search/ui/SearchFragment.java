package com.grouprace.feature.search.ui;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.search.R;
import com.grouprace.feature.search.apdater.SearchAdapter;

import com.grouprace.core.navigation.AppNavigator;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    private SearchAdapter adapter;

    @Inject
    AppNavigator navigator;

    private RecyclerView recyclerView;
    private EditText etSearch;
    private TextView tvTitle;
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    private String lastQuery = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupDebounceSearch();
        setupTabs();
        observeState();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rvSuggestedUsers);
        etSearch = view.findViewById(R.id.etSearch);
        tvTitle = view.findViewById(R.id.tvSectionTitle);
        progressBar = view.findViewById(R.id.progressBar);
        tabLayout = view.findViewById(R.id.tabLayout);

        TopAppBarHelper.setupTopAppBar(view, new TopAppBarConfig.Builder()
                .setTitle("Search")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back, v -> {
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                })
                .build());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new SearchAdapter(new ArrayList<>(), new SearchAdapter.OnUserActionListener() {
            @Override
            public void onActionClick(int userId, boolean isFollowing) {
                boolean isFriendsTab = tabLayout.getSelectedTabPosition() == 0;
                if (isFriendsTab) {
                    if (isFollowing) {
                        viewModel.unfollowUser(userId).observe(getViewLifecycleOwner(), res -> {
                            if (res instanceof Result.Success) {
                                viewModel.updateItemStatus(userId, 0);
                            }
                        });
                    } else {
                        viewModel.followUser(userId).observe(getViewLifecycleOwner(), res -> {
                            if (res instanceof Result.Success) {
                                viewModel.updateItemStatus(userId, 1);
                            }
                        });
                    }
                } else {
                    if (isFollowing) {
                        viewModel.leaveClub(userId).observe(getViewLifecycleOwner(), res -> {
                            if (res instanceof Result.Success) {
                                viewModel.updateItemStatus(userId, 0);
                            }
                        });
                    } else {
                        viewModel.joinClub(userId).observe(getViewLifecycleOwner(), res -> {
                            if (res instanceof Result.Success) {
                                String message = ((Result.Success<String>) res).data;
                                if ("Joined".equalsIgnoreCase(message)) {
                                    viewModel.updateItemStatus(userId, 1);
                                } else if ("Request sent".equalsIgnoreCase(message)) {
                                    viewModel.updateItemStatus(userId, 2);
                                } else {
                                    // Default fallback if message is unexpected
                                    viewModel.updateItemStatus(userId, 1);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onItemClick(int userId) {
                boolean isFriendsTab = tabLayout.getSelectedTabPosition() == 0;
                if (isFriendsTab) {
                    navigator.openUserProfile(SearchFragment.this, userId);
                } else {
                    navigator.openClubDetail(SearchFragment.this, userId);
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void setupDebounceSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    String query = s.toString().trim();

                    if (!query.equals(lastQuery)) {
                        lastQuery = query;
                        viewModel.search(query);
                    }
                };

                handler.postDelayed(searchRunnable, 300);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupTabs() {
        if (viewModel.isClubTab()) {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            if (tab != null) {
                tab.select();
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                boolean isClub = tab.getPosition() == 1;
                viewModel.switchTab(isClub);
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    private void observeState() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            progressBar.setVisibility(state.isLoading ? View.VISIBLE : View.GONE);
            if (state.error != null) {
                Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isFriendsTab = tabLayout.getSelectedTabPosition() == 0;

            if (state.isSearching || !isFriendsTab) {
                tvTitle.setVisibility(View.GONE);
            } else {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText("PEOPLE YOU MAY KNOW");
            }

            adapter.updateData(
                    state.data != null ? state.data : new ArrayList<>(),
                    !isFriendsTab
            );
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }
    }
}