package com.grouprace.feature.posts.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostDetailFragment extends Fragment implements CommentAdapter.OnCommentActionListener {

    private static final String ARG_POST_ID = "post_id";

    private int postId;
    private Post post;
    private PostDetailViewModel viewModel;
    private CommentAdapter commentAdapter;
    private PostAdapter.PostViewHolder postViewHolder;
    private ImageView ivLike;
    private ImageView ivComment;

    private RecyclerView rvComments;
    private ProgressBar loadingBar;
    private TextView tvError;
    private EditText etComment;
    private ImageView btnSend;

    private View layoutReplyIndicator;
    private TextView tvReplyingTo;
    private ImageView btnCancelReply;

    private Integer parentId = null;
    private List<Comment> currentComments = new ArrayList<>();

    public static PostDetailFragment newInstance(int postId) {
        PostDetailFragment fragment = new PostDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getInt(ARG_POST_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_detail, container, false);
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
        loadingBar = view.findViewById(R.id.loading_comments);
        tvError = view.findViewById(R.id.tv_comment_error);
        etComment = view.findViewById(R.id.et_comment);
        btnSend = view.findViewById(R.id.btn_send);

        layoutReplyIndicator = view.findViewById(R.id.layout_reply_indicator);
        tvReplyingTo = view.findViewById(R.id.tv_replying_to);
        btnCancelReply = view.findViewById(R.id.btn_cancel_reply);

        View postHeaderView = view.findViewById(R.id.post_header);
        postViewHolder = new PostAdapter.PostViewHolder(postHeaderView);
        ivLike = postHeaderView.findViewById(R.id.iv_like);
        ivComment = postHeaderView.findViewById(R.id.iv_comment);
    }

    private void setupRecyclerView() {
        commentAdapter = new CommentAdapter(this);
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComments.setAdapter(commentAdapter);
    }

    private void setupReplyBanner() {
        btnCancelReply.setOnClickListener(v -> cancelReply());
    }

    private void cancelReply() {
        parentId = null;
        layoutReplyIndicator.setVisibility(View.GONE);
    }

    private void observeViewModel() {
        viewModel.getPostData().observe(getViewLifecycleOwner(), p -> {
            if (p != null) {
                this.post = p;
                bindPostHeader(p);
            }
        });

        viewModel.getComments().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                loadingBar.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.GONE);
            } else if (result instanceof Result.Success) {
                loadingBar.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
                currentComments = new ArrayList<>(((Result.Success<List<Comment>>) result).data);
                commentAdapter.submitList(currentComments);
            } else if (result instanceof Result.Error) {
                loadingBar.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(((Result.Error<?>) result).message);
            }
        });
    }

    private void bindPostHeader(Post post) {
        postViewHolder.bind(post);

        InteractionAnimator.setupSquishAnimation(ivLike);
        ivLike.setOnClickListener(v -> {
            if (post.isLiked()) {
                viewModel.unlikePost(post.getPostId()).observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Success) {
                        post.setLiked(false);
                        post.setLikeCount(post.getLikeCount() - 1);
                        postViewHolder.bindLikes(post);
                    }
                });
            } else {
                viewModel.likePost(post.getPostId()).observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Success) {
                        post.setLiked(true);
                        post.setLikeCount(post.getLikeCount() + 1);
                        postViewHolder.bindLikes(post);
                    }
                });
            }
        });

        ivComment.setOnClickListener(v -> {
            etComment.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etComment, InputMethodManager.SHOW_IMPLICIT);
        });
    }

    private void setupInput() {
        btnSend.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            if (!content.isEmpty()) {
                viewModel.createComment(content, parentId).observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Success) {
                        etComment.setText("");
                        cancelReply();
                        hideKeyboard();
                        viewModel.loadComments();
                        Toast.makeText(getContext(), "Comment posted", Toast.LENGTH_SHORT).show();
                    } else if (result instanceof Result.Error) {
                        Toast.makeText(getContext(), "Failed: " + ((Result.Error<?>) result).message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void hideKeyboard() {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onLikeClicked(Comment comment, int position) {
        viewModel.likeComment(comment.getCommentId(), !comment.isLiked()).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                 viewModel.loadComments();
            } else if (result instanceof Result.Error) {
                Toast.makeText(getContext(), "Like failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReplyClicked(Comment comment) {
        parentId = comment.getCommentId();
        tvReplyingTo.setText("Replying to @" + comment.getUsername());
        layoutReplyIndicator.setVisibility(View.VISIBLE);
        etComment.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etComment, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onViewRepliesClicked(Comment comment) {
        viewModel.loadReplies(comment.getCommentId()).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                List<Comment> replies = ((Result.Success<List<Comment>>) result).data;
                insertReplies(comment, replies);
            }
        });
    }

    private void insertReplies(Comment parent, List<Comment> replies) {
        int index = currentComments.indexOf(parent);
        if (index != -1) {
            currentComments.removeIf(c -> parent.getCommentId() == (c.getParentId() != null ? c.getParentId() : -1));
            currentComments.addAll(index + 1, replies);
            commentAdapter.submitList(new ArrayList<>(currentComments));
            commentAdapter.notifyItemChanged(index);
        }
    }
}
