package com.grouprace.feature.posts.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.feature.posts.R;
import com.grouprace.feature.posts.ui.adapter.PostAdapter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostFragment extends Fragment {

    private PostViewModel viewModel;
    private EditText editContent;
    private Button btnGetPosts;
    private TextView textStatus;
    private RecyclerView rvPosts;
    private PostAdapter postAdapter;

    public PostFragment() {
        super(R.layout.fragment_post);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editContent = view.findViewById(R.id.edit_post_content);
        btnGetPosts = view.findViewById(R.id.btn_get_posts);
        textStatus = view.findViewById(R.id.text_post_status);

        rvPosts = view.findViewById(R.id.rv_posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        postAdapter = new PostAdapter();
        rvPosts.setAdapter(postAdapter);

        viewModel = new ViewModelProvider(this).get(PostViewModel.class);

        btnGetPosts.setOnClickListener(v -> {
            viewModel.fetchPosts();
        });

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                btnGetPosts.setText("Fetching...");
                btnGetPosts.setEnabled(false);
            } else {
                btnGetPosts.setText("Get Posts");
                btnGetPosts.setEnabled(true);
            }
        });

        viewModel.getPostStatus().observe(getViewLifecycleOwner(), statusMessage -> {
            textStatus.setText(statusMessage);
            if ("Post published successfully!".equals(statusMessage)) {
                editContent.setText(""); 
            }
        });

        // Observe the posts from the repository
        viewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                postAdapter.submitList(posts);
            }
        });
    }
}
