package com.grouprace.feature.posts.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Comment;
import com.grouprace.core.model.Post;
import com.grouprace.core.system.animation.InteractionAnimator;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.posts.R;
import com.grouprace.feature.posts.ui.adapter.CommentAdapter;
import com.grouprace.feature.posts.ui.adapter.PostAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostDetailFragment extends Fragment {

    private static final String ARG_POST_ID = "post_id";

    private int postId;
    private Post post;
    private PostDetailViewModel viewModel;
    private PostHeaderViewHolder postViewHolder;

    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;

    private ProgressBar progressBar;
    private TextView tvErrorMessage;

    private EditText etComment;
    private ImageView btnSend;

    private Integer parentId = null;
    private Integer pendingExpandParentId = null;
    private List<Comment> currentComments = new ArrayList<>();

    private View replyBanner;
    private TextView tvReplyingTo;
    private ImageView btnCancelReply;

    public static PostDetailFragment newInstance(int postId) {
        PostDetailFragment fragment = new PostDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    public PostDetailFragment() {
        super(R.layout.fragment_post_detail);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getInt(ARG_POST_ID);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTopBar(view);
        initViews(view);
        setupRecyclerView();

        viewModel = new ViewModelProvider(this).get(PostDetailViewModel.class);
        viewModel.setPostId(postId);

        observeViewModel();
        setupInput();
        setupReplyBanner();
    }

    private void setupTopBar(View view) {
        TopAppBarHelper.setupTopAppBar(view, new TopAppBarConfig.Builder()
                .setTitle("Post Details")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back, v -> requireActivity().onBackPressed())
                .build());
    }

    private void initViews(View view) {
        rvComments = view.findViewById(R.id.rv_comments);

        etComment = view.findViewById(R.id.et_comment);
        btnSend = view.findViewById(R.id.btn_send);

        progressBar = view.findViewById(R.id.loading_comments);
        tvErrorMessage = view.findViewById(R.id.tv_comment_error);

        View postHeaderView = view.findViewById(R.id.post_header);
        postViewHolder = new PostHeaderViewHolder(postHeaderView);

        // init reply banner
        replyBanner = view.findViewById(R.id.layout_reply_indicator);
        tvReplyingTo = replyBanner.findViewById(R.id.tv_replying_to);
        btnCancelReply = replyBanner.findViewById(R.id.btn_cancel_reply);
    }

    private void setupRecyclerView() {
        commentAdapter = new CommentAdapter(new CommentAdapter.OnCommentActionListener() {
            @Override
            public void onReplyClicked(Comment comment) {
                parentId = comment.getCommentId();
                tvReplyingTo.setText("Replying to @" + comment.getUsername());
                replyBanner.setVisibility(View.VISIBLE);
                focusCommentInput();
            }

            @Override
            public void onLikeClicked(Comment comment, int position) {
                // TODO: Xử lý khi bấm nút Like
                // Optimistic UI update
                if (position >= 0 && position < currentComments.size()) {
                    Comment old = currentComments.get(position);
                    // Verify it's the same comment to be safe
                    if (old.getCommentId() == comment.getCommentId()) {
                        boolean newLiked = !old.isLiked();
                        int newCount = old.getLikeCount() + (newLiked ? 1 : -1);

                        // Re-create comment object with new values (immutable approach)
                        Comment updated = new Comment(
                                old.getCommentId(), old.getPostId(), old.getUserId(), old.getContent(),
                                old.getCreatedAt(), old.getUsername(), old.getFullName(), old.getAvatarUrl(), newCount,
                                old.getReplyCount(), newLiked, old.getParentId());

                        currentComments.set(position, updated);
                        commentAdapter.submitList(new ArrayList<>(currentComments));
                    }
                }

                viewModel.likeComment(postId, comment.getCommentId(), !comment.isLiked())
                        .observe(getViewLifecycleOwner(), result -> {
                            if (result instanceof Result.Error) {
                                // Revert on error
                                viewModel.loadComments();
                                Toast.makeText(getContext(), "Like failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onViewRepliesClicked(Comment comment) {
                viewModel.loadReplies(postId, comment.getCommentId()).observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Success) {
                        List<Comment> replies = ((Result.Success<List<Comment>>) result).data;
                        insertReplies(comment, replies);
                    }
                });
            }

            private void insertReplies(Comment parent, List<Comment> replies) {
                int index = currentComments.indexOf(parent);
                if (index != -1) {
                    // Remove existing replies from this parent first to avoid duplicates if clicked
                    // multiple times
                    currentComments
                            .removeIf(c -> parent.getCommentId() == (c.getParentId() != null ? c.getParentId() : -1));
                    currentComments.addAll(index + 1, replies);
                    commentAdapter.submitList(new ArrayList<>(currentComments));
                    commentAdapter.notifyItemChanged(index); // Refresh parent to maybe hide "View X replies" or change it to
                                                      // "Hide"
                }
            }
        });

        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComments.setAdapter(commentAdapter);
        rvComments.setNestedScrollingEnabled(false);
    }

    private void setupReplyBanner() {
        replyBanner.setVisibility(View.GONE);
        btnCancelReply.setOnClickListener(v -> cancelReply());
    }

    private void cancelReply() {
        parentId = null;
        replyBanner.setVisibility(View.GONE);
    }

    private void observeViewModel() {
        viewModel.getPostData().observe(getViewLifecycleOwner(), p -> {
            if (p != null) {
                this.post = p;
                bindPostHeader(p);
            }
        });

        viewModel.getComments().observe(getViewLifecycleOwner(), this::handleCommentsResult);
    }

    private void handleCommentsResult(Result<List<Comment>> result) {
        if (result instanceof Result.Loading) {
            progressBar.setVisibility(View.VISIBLE);
            tvErrorMessage.setVisibility(View.GONE);
        } else if (result instanceof Result.Success) {
            progressBar.setVisibility(View.GONE);
            tvErrorMessage.setVisibility(View.GONE);
            currentComments = new ArrayList<>(((Result.Success<List<Comment>>) result).data);
            commentAdapter.submitList(currentComments);
        } else if (result instanceof Result.Error) {
            progressBar.setVisibility(View.GONE);
            tvErrorMessage.setVisibility(View.VISIBLE);
            tvErrorMessage.setText(((Result.Error<?>) result).message);
        }
    }

    private void bindPostHeader(Post post) {
        postViewHolder.bind(post);

        InteractionAnimator.setupSquishAnimation(postViewHolder.ivLike);
        postViewHolder.ivLike.setOnClickListener(v -> handleLikeClick(post));

        InteractionAnimator.setupSquishAnimation(postViewHolder.ivComment);
        postViewHolder.ivComment.setOnClickListener(v -> focusCommentInput());
    }

    private void handleLikeClick(Post post) {
        LiveData<Result<Boolean>> likeAction = post.isLiked()
                ? viewModel.unlikePost(post.getPostId())
                : viewModel.likePost(post.getPostId());

        likeAction.observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                post.setLiked(!post.isLiked());
                post.setLikeCount(post.isLiked() ? post.getLikeCount() + 1 : Math.max(0, post.getLikeCount() - 1));
                postViewHolder.bindLikes(post);
            }
        });
    }

    private void focusCommentInput() {
        etComment.requestFocus();
        showKeyboard();
    }

    private void setupInput() {
        btnSend.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            if (content.isEmpty())
                return;

            final Integer targetParentId = parentId;

            viewModel.createComment(postId, content, parentId).observe(getViewLifecycleOwner(), result -> {
                if (result instanceof Result.Success) {
                    pendingExpandParentId = targetParentId;
                    clearCommentInput();
                    cancelReply();
                    viewModel.loadComments();
                    Toast.makeText(getContext(), "Comment posted", Toast.LENGTH_SHORT).show();
                } else if (result instanceof Result.Error) {
                    Toast.makeText(getContext(), "Failed: " + ((Result.Error<?>) result).message,
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void clearCommentInput() {
        etComment.setText("");
        cancelReply();
        hideKeyboard();
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etComment, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}