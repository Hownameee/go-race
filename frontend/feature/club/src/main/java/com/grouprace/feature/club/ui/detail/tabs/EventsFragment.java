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
import com.grouprace.core.model.ClubEvent;
import com.grouprace.feature.club.R;
import com.grouprace.feature.club.ui.ClubDetailViewModel;
import com.grouprace.feature.club.ui.adapter.ClubEventAdapter;

import java.util.List;

public class EventsFragment extends Fragment {

    private ClubDetailViewModel viewModel;
    private ClubEventAdapter adapter;

    public EventsFragment() {
        super(R.layout.fragment_club_detail_events);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireParentFragment()).get(ClubDetailViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_events);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClubEventAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getEvents().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                adapter.submitList(((Result.Success<List<ClubEvent>>) result).data);
            }
        });
    }
}
