package com.grouprace.feature.profile.ui.posts;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Post;
import com.grouprace.feature.profile.R;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfilePostsFragment extends Fragment {
    private static final String ARG_USER_ID = "arg_user_id";
    private static final String ARG_PROFILE_NAME = "arg_profile_name";
    private static final String ARG_IS_SELF = "arg_is_self";

    private ProfilePostsViewModel viewModel;
    private ProfilePostAdapter adapter;
    private ProgressBar loadingState;
    private TextView errorState;
    private TextView emptyState;
    private RecyclerView recyclerView;

    public ProfilePostsFragment() {
        super(R.layout.fragment_profile_posts);
    }

    public static ProfilePostsFragment newInstance(int userId, @Nullable String profileName, boolean isSelf) {
        ProfilePostsFragment fragment = new ProfilePostsFragment();
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

        ImageButton backButton = view.findViewById(R.id.profile_posts_back_button);
        TextView titleView = view.findViewById(R.id.profile_posts_title);
        loadingState = view.findViewById(R.id.profile_posts_loading_state);
        errorState = view.findViewById(R.id.profile_posts_error_state);
        emptyState = view.findViewById(R.id.profile_posts_empty_state);
        recyclerView = view.findViewById(R.id.profile_posts_recycler_view);

        titleView.setText(isSelf ? "Posts" : (profileName != null && !profileName.isEmpty() ? profileName + "'s Posts" : "Posts"));
        backButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        adapter = new ProfilePostAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ProfilePostsViewModel.class);
        viewModel.initialize(userId, isSelf);
        viewModel.getPostsResult().observe(getViewLifecycleOwner(), this::bindPostsState);
        viewModel.sync();
    }

    private void bindPostsState(Result<List<Post>> result) {
        if (result instanceof Result.Loading) {
            loadingState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            errorState.setVisibility(View.GONE);
            emptyState.setVisibility(View.GONE);
        } else if (result instanceof Result.Success) {
            loadingState.setVisibility(View.GONE);
            errorState.setVisibility(View.GONE);

            List<Post> posts = ((Result.Success<List<Post>>) result).data;
            adapter.submitList(posts);

            boolean isEmpty = posts == null || posts.isEmpty();
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        } else if (result instanceof Result.Error) {
            loadingState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.GONE);
            errorState.setVisibility(View.VISIBLE);
            String message = ((Result.Error<List<Post>>) result).message;
            errorState.setText(message != null ? message : "Failed to load posts.");
        }
    }
}
