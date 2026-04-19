package com.grouprace.feature.profile.ui.main.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Record;
import com.grouprace.feature.profile.R;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileRecordsFragment extends Fragment {
    private static final String ARG_USER_ID = "arg_user_id";
    private static final String ARG_PROFILE_NAME = "arg_profile_name";
    private static final String ARG_IS_SELF = "arg_is_self";

    private ProfileRecordsViewModel viewModel;
    private ProfileRecordAdapter adapter;
    private RecyclerView recyclerView;
    private View loadingView;
    private View errorView;
    private TextView errorText;
    private TextView emptyText;
    private Button retryButton;
    private boolean isFirst = true;

    public ProfileRecordsFragment() {
        super(R.layout.fragment_profile_records);
    }

    public static ProfileRecordsFragment newInstance(int userId, @Nullable String profileName, boolean isSelf) {
        ProfileRecordsFragment fragment = new ProfileRecordsFragment();
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

        viewModel = new ViewModelProvider(this).get(ProfileRecordsViewModel.class);
        viewModel.initialize(userId);

        ImageButton backButton = view.findViewById(R.id.profile_records_back_button);
        TextView titleView = view.findViewById(R.id.profile_records_title);
        recyclerView = view.findViewById(R.id.profile_records_recycler_view);
        loadingView = view.findViewById(R.id.profile_records_loading_state);
        errorText = view.findViewById(R.id.profile_records_error_state);
        emptyText = view.findViewById(R.id.profile_records_empty_state);
        errorView = view.findViewById(R.id.ll_error);
        retryButton = view.findViewById(R.id.btn_retry);

        titleView.setText(isSelf ? "Activities" : (profileName != null && !profileName.isEmpty() ? profileName + "'s Activities" : "Activities"));
        backButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        retryButton.setOnClickListener(v -> viewModel.sync());

        adapter = new ProfileRecordAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getRecords().observe(getViewLifecycleOwner(), this::displayRecords);

        viewModel.getSyncStatus().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                if (adapter.isEmpty()) {
                    loadingView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    errorText.setVisibility(View.GONE);
                    emptyText.setVisibility(View.GONE);
                    errorView.setVisibility(View.GONE);
                }
            } else if (result instanceof Result.Success) {
                loadingView.setVisibility(View.GONE);

                if (adapter.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    errorText.setVisibility(View.GONE);
                    errorView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    errorText.setVisibility(View.GONE);
                    emptyText.setVisibility(View.GONE);
                    errorView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else if (result instanceof Result.Error) {
                if (adapter.isEmpty()) {
                    loadingView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText(((Result.Error<Boolean>) result).message);
                    retryButton.setVisibility(View.VISIBLE);
                }
            }
        });

        if (isFirst) {
            isFirst = false;
            viewModel.sync();
        }
    }

    private void displayRecords(List<Record> records) {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);
        adapter.submitList(records);

        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && adapter.getRecordCount() > 0) {
                    viewModel.loadMore(adapter.getRecordCount());
                }
            }
        });
    }
}
