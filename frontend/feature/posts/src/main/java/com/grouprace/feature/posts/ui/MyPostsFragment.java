package com.grouprace.feature.posts.ui;

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

import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Post;
import com.grouprace.feature.posts.R;
import com.grouprace.feature.posts.ui.adapter.PostAdapter;

import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyPostsFragment extends Fragment {

  private MyPostsViewModel viewModel;
  private PostAdapter postAdapter;
  private ProgressBar progressBar;
  private TextView errorState;
  private TextView emptyState;
  private RecyclerView recyclerView;

  public MyPostsFragment() {
    super(R.layout.fragment_my_posts);
  }

  public static MyPostsFragment newInstance() {
    return new MyPostsFragment();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ImageButton backButton = view.findViewById(R.id.my_posts_back_button);
    progressBar = view.findViewById(R.id.my_posts_loading_state);
    errorState = view.findViewById(R.id.my_posts_error_state);
    recyclerView = view.findViewById(R.id.my_posts_recycler_view);

    backButton.setOnClickListener(v -> requireActivity().onBackPressed());

    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    emptyState = view.findViewById(R.id.my_posts_empty_state);
    postAdapter = new PostAdapter();
    postAdapter.setOnPostActionListener(new PostAdapter.OnPostActionListener() {
      @Override
      public void onLikeClicked(Post post, int position) {
        if (post.isLiked()) {
          viewModel.unlikePost(post.getPostId()).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
              post.setLiked(false);
              post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
              postAdapter.notifyItemChanged(position);
            }
          });
        } else {
          viewModel.likePost(post.getPostId()).observe(getViewLifecycleOwner(), result -> {
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

      @Override
      public void onShareClicked(Post post) {
        double distance = post.getDistanceKm() != null ? post.getDistanceKm() : 0.0;
        int seconds = post.getDurationSeconds() != null ? post.getDurationSeconds() : 0;
        String pace;
        if (distance > 0 && seconds > 0) {
          double paceMinKm = (seconds / 60.0) / distance;
          int paceMin = (int) paceMinKm;
          int paceSec = (int) ((paceMinKm - paceMin) * 60);
          pace = String.format(Locale.getDefault(), "%d:%02d /km", paceMin, paceSec);
        } else {
          pace = "--:--";
        }

        double speedVal = post.getSpeed() != null ? post.getSpeed() : 0.0;
        String speedStr = String.format(Locale.getDefault(), "%.1f km/h", speedVal);

        ShareActivityFragment.newInstance(
            post.getTitle(),
            String.format(Locale.getDefault(), "%.2f km", distance),
            pace,
            TimeUtils.formatDuration(seconds),
            post.getFullName(),
            post.getRecordImageUrl(),
            speedStr
        ).show(getChildFragmentManager(), "ShareBottomSheet");
      }

            @Override
            public void onReportClicked(Post post) {
            }
    });
    recyclerView.setAdapter(postAdapter);

    viewModel = new ViewModelProvider(this).get(MyPostsViewModel.class);
    viewModel.getMyPosts().observe(getViewLifecycleOwner(), this::bindState);
    viewModel.loadMyPosts();
  }

  private void bindState(Result<List<Post>> result) {
    if (result instanceof Result.Loading) {
      progressBar.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
      errorState.setVisibility(View.GONE);
      emptyState.setVisibility(View.GONE);
    } else if (result instanceof Result.Success) {
      List<Post> posts = ((Result.Success<List<Post>>) result).data;
      progressBar.setVisibility(View.GONE);
      errorState.setVisibility(View.GONE);
      postAdapter.submitList(posts);

      boolean isEmpty = posts == null || posts.isEmpty();
      recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
      emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    } else if (result instanceof Result.Error) {
      progressBar.setVisibility(View.GONE);
      recyclerView.setVisibility(View.GONE);
      emptyState.setVisibility(View.GONE);
      errorState.setVisibility(View.VISIBLE);
      String message = ((Result.Error<List<Post>>) result).message;
      errorState.setText(message != null ? message : "Failed to load posts.");
    }
  }
}
