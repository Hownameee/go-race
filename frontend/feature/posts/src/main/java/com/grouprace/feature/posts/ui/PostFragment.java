package com.grouprace.feature.posts.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.feature.posts.R;
import com.grouprace.feature.posts.ui.adapter.PostAdapter;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Post;
import java.util.List;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostFragment extends Fragment {

    private PostViewModel viewModel;
    private RecyclerView rvPosts;
    private PostAdapter postAdapter;
    private ProgressBar progressBar;
    private TextView tvError;
    private boolean isLoadingPage = false;

    public PostFragment() {
        super(R.layout.fragment_post);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rv_posts);
        progressBar = view.findViewById(R.id.loading_state);
        tvError = view.findViewById(R.id.error_state);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rvPosts.setLayoutManager(layoutManager);
        postAdapter = new PostAdapter();
        postAdapter.setOnPostActionListener(new PostAdapter.OnPostActionListener() {
            @Override
            public void onLikeClicked(Post post, int position) {
                // Hardcoded userId = 1 for now. Replace with actual logged-in user ID later.
                int userId = 1;

                if (post.isLiked()) {
                    viewModel.unlikePost(post.getPostId(), userId).observe(getViewLifecycleOwner(), result -> {
                        if (result instanceof Result.Success) {
                            post.setLiked(false);
                            post.setLikeCount(post.getLikeCount() - 1);
                            postAdapter.notifyItemChanged(position);
                        }
                    });
                } else {
                    viewModel.likePost(post.getPostId(), userId).observe(getViewLifecycleOwner(), result -> {
                        if (result instanceof Result.Success) {
                            post.setLiked(true);
                            post.setLikeCount(post.getLikeCount() + 1);
                            postAdapter.notifyItemChanged(position);
                        }
                    });
                }
            }

            @Override
            public void onCommentClicked(Post post) {
                CommentFragment.newInstance(post.getPostId())
                        .show(getChildFragmentManager(), "CommentBottomSheet");
            }
        });
        rvPosts.setAdapter(postAdapter);

        viewModel = new ViewModelProvider(this).get(PostViewModel.class);

        observeViewModel();
        setupInfiniteScroll(layoutManager);
    }

    private void observeViewModel() {
        viewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                postAdapter.submitList(posts);
                if (!posts.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    rvPosts.setVisibility(View.VISIBLE);
                    tvError.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getSyncStatus().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result instanceof Result.Loading) {
                if (postAdapter.getItemCount() == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    rvPosts.setVisibility(View.GONE);
                }
                tvError.setVisibility(View.GONE);
                isLoadingPage = true;

            } else if (result instanceof Result.Success) {
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
                rvPosts.setVisibility(View.VISIBLE);
                isLoadingPage = false;

            } else if (result instanceof Result.Error) {
                progressBar.setVisibility(View.GONE);
                isLoadingPage = false;

                if (postAdapter.getItemCount() == 0) {
                    rvPosts.setVisibility(View.GONE);
                    tvError.setVisibility(View.VISIBLE);
                    String errorMessage = ((Result.Error<?>) result).message;
                    tvError.setText(errorMessage != null ? errorMessage : "Check your connection.");
                } else {
                    // android.widget.Toast.makeText(getContext(), "Sync failed", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupInfiniteScroll(LinearLayoutManager layoutManager) {
        rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (dy > 0 && !isLoadingPage) {
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    isLoadingPage = true;

                    String cursor = postAdapter.getLastPostCreatedAt();
                    if (cursor != null) {
                        viewModel.fetchPosts(cursor);
                    }
                }
            }
            }
        });
    }
}
