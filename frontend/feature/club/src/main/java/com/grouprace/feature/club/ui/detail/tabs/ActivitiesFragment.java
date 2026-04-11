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
import com.grouprace.core.model.Record;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.ClubDetailViewModel;
import com.grouprace.feature.club.ui.adapter.ClubActivityAdapter;

import java.util.List;

public class ActivitiesFragment extends Fragment {

    private ClubDetailViewModel viewModel;
    private ClubActivityAdapter adapter;

    public ActivitiesFragment() {
        super(R.layout.fragment_club_detail_activities);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireParentFragment()).get(ClubDetailViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_activities);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClubActivityAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getActivities().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                adapter.submitList(((Result.Success<List<Record>>) result).data);
            }
        });
    }
}
