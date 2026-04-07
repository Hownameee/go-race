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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Comment;
import com.grouprace.feature.posts.R;
import com.grouprace.feature.posts.ui.adapter.CommentAdapter;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CommentFragment extends BottomSheetDialogFragment implements CommentAdapter.OnCommentActionListener {

    private static final String ARG_POST_ID = "post_id";

    private int postId;
    private CommentViewModel viewModel;
    private CommentAdapter adapter;
    
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

    public static CommentFragment newInstance(int postId) {
        CommentFragment fragment = new CommentFragment();
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
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvComments = view.findViewById(R.id.rv_comments);
        loadingBar = view.findViewById(R.id.loading_comments);
        tvError = view.findViewById(R.id.tv_comment_error);
        etComment = view.findViewById(R.id.et_comment);
        btnSend = view.findViewById(R.id.btn_send);

        layoutReplyIndicator = view.findViewById(R.id.layout_reply_indicator);
        tvReplyingTo = view.findViewById(R.id.tv_replying_to);
        btnCancelReply = view.findViewById(R.id.btn_cancel_reply);

        adapter = new CommentAdapter(this);
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComments.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        viewModel.loadComments(postId);

        observeViewModel();
        setupInput();
        setupReplyBanner();
    }

    private void setupReplyBanner() {
        btnCancelReply.setOnClickListener(v -> cancelReply());
    }

    private void cancelReply() {
        parentId = null;
        layoutReplyIndicator.setVisibility(View.GONE);
    }

    private void observeViewModel() {
        viewModel.getComments().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                loadingBar.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.GONE);
            } else if (result instanceof Result.Success) {
                loadingBar.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
                currentComments = new ArrayList<>(((Result.Success<List<Comment>>) result).data);
                adapter.submitList(currentComments);
            } else if (result instanceof Result.Error) {
                loadingBar.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(((Result.Error<?>) result).message);
            }
        });
    }

    private void setupInput() {
        btnSend.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            if (!content.isEmpty()) {
                viewModel.createComment(postId, content, parentId).observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Success) {
                        etComment.setText("");
                        Integer finishedParentId = parentId; // Capture for reply refresh
                        cancelReply();
                        hideKeyboard();
                        
                        if (finishedParentId == null) {
                            viewModel.loadComments(postId); // Refresh all for new top-level
                        } else {
                            // Find parent and refresh its replies specifically
                            for (Comment c : currentComments) {
                                if (c.getCommentId() == finishedParentId) {
                                    onViewRepliesClick(c);
                                    break;
                                }
                            }
                        }
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
    public void onLikeClick(Comment comment) {
        // Optimistic UI update
        int index = currentComments.indexOf(comment);
        if (index != -1) {
            Comment old = currentComments.get(index);
            boolean newLiked = !old.isLiked();
            int newCount = old.getLikeCount() + (newLiked ? 1 : -1);
            
            // Re-create comment object with new values (immutable approach)
            Comment updated = new Comment(
                old.getCommentId(), old.getPostId(), old.getUserId(), old.getContent(),
                old.getCreatedAt(), old.getUsername(), old.getFullName(), old.getAvatarUrl(), newCount,
                    old.getReplyCount(), newLiked, old.getParentId()

            );
            
            currentComments.set(index, updated);
            adapter.submitList(new ArrayList<>(currentComments));
        }

        viewModel.likeComment(postId, comment.getCommentId(), !comment.isLiked()).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Error) {
                // Revert on error
                viewModel.loadComments(postId); 
                Toast.makeText(getContext(), "Like failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReplyClick(Comment comment) {
        parentId = comment.getCommentId();
        tvReplyingTo.setText("Replying to @" + comment.getUsername());
        layoutReplyIndicator.setVisibility(View.VISIBLE);
        etComment.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etComment, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onViewRepliesClick(Comment comment) {
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
            // Remove existing replies from this parent first to avoid duplicates if clicked multiple times
            currentComments.removeIf(c -> parent.getCommentId() == (c.getParentId() != null ? c.getParentId() : -1));
            currentComments.addAll(index + 1, replies);
            adapter.submitList(new ArrayList<>(currentComments));
            adapter.notifyItemChanged(index); // Refresh parent to maybe hide "View X replies" or change it to "Hide"
        }
    }
}
