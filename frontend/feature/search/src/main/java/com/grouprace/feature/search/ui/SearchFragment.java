package com.grouprace.feature.search.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.UserSearchResult;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.search.R;
import com.grouprace.feature.search.apdater.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    private SearchAdapter adapter;

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private TextView tvSectionTitle;
    private EditText etSearch;

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
        observeViewModel();
        setupListeners();
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerView = view.findViewById(R.id.rvSuggestedUsers);
        tvSectionTitle = view.findViewById(R.id.tvSectionTitle);
        etSearch = view.findViewById(R.id.etSearch);

        etSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        etSearch.setSingleLine(true);

        TopAppBarHelper.setupTopAppBar(view, new TopAppBarConfig.Builder()
                .setTitle("Search")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back, v -> {
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                })
                .build());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new SearchAdapter(new ArrayList<>(), (userId, isFollowingNow) -> {
            if (isFollowingNow) {
                viewModel.unfollowUser(userId).observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Success) {
                        adapter.updateUserStatus(userId, false);
                    } else if (result instanceof Result.Error) {
                        Toast.makeText(requireContext(), "Error unfollow", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                viewModel.followUser(userId).observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Success) {
                        adapter.updateUserStatus(userId, true);
                    } else if (result instanceof Result.Error) {
                        Toast.makeText(requireContext(), "Error follow", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getIsClubTab().observe(getViewLifecycleOwner(), isClub -> {
            tvSectionTitle.setVisibility(isClub ? View.GONE : View.VISIBLE);
            etSearch.setHint(isClub ? "Find a club..." : "Search on Gorace");
            etSearch.setText("");
        });

        viewModel.getSuggestedFriends().observe(getViewLifecycleOwner(), res -> handleDataResult(res, false));
        viewModel.getSearchFriendsResults().observe(getViewLifecycleOwner(), res -> handleDataResult(res, false));
        viewModel.getSuggestedClubs().observe(getViewLifecycleOwner(), res -> handleDataResult(res, true));
        viewModel.getSearchClubsResults().observe(getViewLifecycleOwner(), res -> handleDataResult(res, true));
    }

    private void handleDataResult(Result<List<UserSearchResult>> result, boolean isForClub) {
        Boolean currentIsClub = viewModel.getIsClubTab().getValue();
        if (currentIsClub == null || isForClub != currentIsClub) return;

        if (result instanceof Result.Success) {
            List<UserSearchResult> data = ((Result.Success<List<UserSearchResult>>) result).data;
            adapter.updateData(data != null ? data : new ArrayList<>(), isForClub);
        } else if (result instanceof Result.Error) {
            String msg = ((Result.Error<?>) result).message;
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            adapter.updateData(new ArrayList<>(), isForClub);
        } else if (result instanceof Result.Loading) {
            adapter.updateData(new ArrayList<>(), isForClub);
        }
    }
    private void setupListeners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) viewModel.fetchSuggestedFriends();
                else viewModel.fetchSuggestedClubs();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        viewModel.search(query);
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        etSearch.clearFocus();
    }
}