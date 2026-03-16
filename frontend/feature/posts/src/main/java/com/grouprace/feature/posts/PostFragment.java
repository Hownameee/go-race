package com.grouprace.feature.posts;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.grouprace.feature.posts.R; // Make sure to import your module's R class!

public class PostFragment extends Fragment {

    private PostViewModel viewModel;
    private EditText editContent;
    private Button btnSubmit;
    private TextView textStatus;

    // Tell the Fragment which XML file to use
    public PostFragment() {
        super(R.layout.fragment_post);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Bind your XML views to Java variables
        editContent = view.findViewById(R.id.edit_post_content);
        btnSubmit = view.findViewById(R.id.btn_submit_post);
        textStatus = view.findViewById(R.id.text_post_status);

        // 2. Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(PostViewModel.class);

        // 3. Set up the Button Click
        btnSubmit.setOnClickListener(v -> {
            String content = editContent.getText().toString();
            viewModel.submitPost(content); // Pass the action to the ViewModel
        });

        // 4. Observe the LiveData from the ViewModel
        observeViewModel();
    }

    private void observeViewModel() {
        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                btnSubmit.setText("Publishing...");
                btnSubmit.setEnabled(false);
            } else {
                btnSubmit.setText("Publish Post");
                btnSubmit.setEnabled(true);
            }
        });

        // Observe status messages
        viewModel.getPostStatus().observe(getViewLifecycleOwner(), statusMessage -> {
            textStatus.setText(statusMessage);
            if (statusMessage.equals("Post published successfully!")) {
                editContent.setText(""); // Clear the input field on success
            }
        });
    }
}