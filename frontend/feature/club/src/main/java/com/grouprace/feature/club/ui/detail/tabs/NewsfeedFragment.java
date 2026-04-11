package com.grouprace.feature.club.ui.detail.tabs;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Post;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.ClubDetailViewModel;
import com.grouprace.feature.club.ui.adapter.ClubPostAdapter;

import java.util.List;

public class NewsfeedFragment extends Fragment {

    private ClubDetailViewModel viewModel;
    private ClubPostAdapter adapter;

    public NewsfeedFragment() {
        super(R.layout.fragment_club_detail_newsfeed);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Use parent fragment's ViewModel to share data
        viewModel = new ViewModelProvider(requireParentFragment()).get(ClubDetailViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_newsfeed);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClubPostAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getPosts().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                adapter.submitList(((Result.Success<List<Post>>) result).data);
            }
        });
    }
}
