package com.grouprace.feature.posts.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.grouprace.core.model.Record;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.posts.R;
import com.grouprace.feature.posts.ui.adapter.RecordSelectorAdapter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddPostFragment extends Fragment {

    public static final String ARG_WITH_ACTIVITY = "with_activity";
    public static final String ARG_CLUB_ID = "club_id";

    private AddPostViewModel viewModel;
    private EditText etTitle;
    private EditText etDescription;
    private View btnAddPhoto;
    private TextView tvRecordHeader;
    private RecyclerView rvRecords;
    private ProgressBar loadingRecords;
    private RecordSelectorAdapter recordAdapter;

    private boolean withActivity;
    private Integer selectedRecordId = null;
    private Integer clubId = null;

    @Inject
    AppNavigator appNavigator;

    public static AddPostFragment newInstance(boolean withActivity) {
        return newInstance(withActivity, null);
    }

    public static AddPostFragment newInstance(boolean withActivity, Integer clubId) {
        AddPostFragment fragment = new AddPostFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_WITH_ACTIVITY, withActivity);
        if (clubId != null) {
            args.putInt(ARG_CLUB_ID, clubId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            withActivity = getArguments().getBoolean(ARG_WITH_ACTIVITY);
            if (getArguments().containsKey(ARG_CLUB_ID)) {
                clubId = getArguments().getInt(ARG_CLUB_ID);
            }
        }
        viewModel = new ViewModelProvider(this).get(AddPostViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupTopBar(view);
        
        if (withActivity) {
            setupRecordsList();
        }

        btnAddPhoto.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Photo upload coming soon!", Toast.LENGTH_SHORT).show()
        );
    }

    private void initViews(View view) {
        etTitle = view.findViewById(R.id.et_title);
        etDescription = view.findViewById(R.id.et_description);
        btnAddPhoto = view.findViewById(R.id.btn_add_photo);
        tvRecordHeader = view.findViewById(R.id.tv_record_header);
        rvRecords = view.findViewById(R.id.rv_records);
        loadingRecords = view.findViewById(R.id.loading_records);
    }

    private void setupTopBar(View view) {
        TopAppBarConfig.Builder builder = new TopAppBarConfig.Builder()
                .setTitle("Add Post")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back, v -> getParentFragmentManager().popBackStack())
                .addRightIcon(com.grouprace.core.system.R.drawable.ic_check, v -> handlePublish());

        TopAppBarHelper.setupTopAppBar(view, builder.build());
    }

    private void setupRecordsList() {
        tvRecordHeader.setVisibility(View.VISIBLE);
        rvRecords.setVisibility(View.VISIBLE);
        loadingRecords.setVisibility(View.VISIBLE);

        recordAdapter = new RecordSelectorAdapter(record -> {
            selectedRecordId = record.getRecordId();
            Log.d("AddPostFragment", "Selected record: " + selectedRecordId);
        });
        rvRecords.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecords.setAdapter(recordAdapter);

        viewModel.getTodayRecords().observe(getViewLifecycleOwner(), records -> {
            loadingRecords.setVisibility(View.GONE);
            if (records != null && !records.isEmpty()) {
                recordAdapter.submitList(records);
            } else {
                tvRecordHeader.setText("No activities recorded today");
            }
        });
    }

    private void handlePublish() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (withActivity && selectedRecordId == null) {
            Toast.makeText(getContext(), "Please select an activity", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.createPost(title, description, selectedRecordId, clubId).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                // Optionally show a loading dialog or state
                Log.d("AddPostFragment", "Publishing post...");
            } else if (result instanceof Result.Success) {
                Toast.makeText(getContext(), "Post published!", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            } else if (result instanceof Result.Error) {
                String error = ((Result.Error<?>) result).message;
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
