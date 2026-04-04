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
import com.grouprace.feature.search.R;
import com.grouprace.feature.search.apdater.SearchAdapter;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    private SearchAdapter adapter;

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
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new SearchAdapter(new ArrayList<>(), (userId, isFollowing) -> {
            if (isFollowing) {
                viewModel.unfollowUser(userId).observe(getViewLifecycleOwner(), res -> {
                    if (res instanceof Result.Success) {
                        adapter.updateUserStatus(userId, false);
                    }
                });
            } else {
                viewModel.followUser(userId).observe(getViewLifecycleOwner(), res -> {
                    if (res instanceof Result.Success) {
                        adapter.updateUserStatus(userId, true);
                    }
                });
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

                    if (query.length() < 2) {
                        lastQuery = "";
                        viewModel.search(""); // load suggested
                        return;
                    }

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
                    false
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