package com.grouprace.feature.records.list.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Record;
import com.grouprace.feature.records.R;
import com.grouprace.feature.records.detail.ui.RecordDetailFragment;
import com.grouprace.feature.records.list.ui.adapter.RecordAdapter;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for displaying a paginated list of records.
 * Supports refreshing, loading more data, and handling error states.
 */
@AndroidEntryPoint
public class RecordsFragment extends Fragment {
    private boolean isFirst = true;
    private RecordsViewModel viewModel;
    private RecordAdapter adapter;
    private ListView listRecord;
    private LinearLayout loadingLayout;
    private LinearLayout errorLayout;
    private TextView errorText;
    private Button retryButton;

    public RecordsFragment() {
        super(R.layout.fragment_list_record);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        listRecord = view.findViewById(R.id.lv_record_list);
        loadingLayout = view.findViewById(R.id.ll_loading);
        errorLayout = view.findViewById(R.id.ll_error);
        errorText = view.findViewById(R.id.tv_error_message);
        retryButton = view.findViewById(R.id.btn_retry);
        retryButton.setOnClickListener(v -> viewModel.fetchRecords(0));

        setupAdapter();
        viewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        observeViewModel();

        setupScrollListener();
    }

    private void setupAdapter() {
        adapter = new RecordAdapter(requireContext(), new ArrayList<>());
        listRecord.setAdapter(adapter);
        listRecord.setOnItemClickListener(this::onRecordClick);
    }

    private void observeViewModel() {
        viewModel.getRecords().observe(getViewLifecycleOwner(), records -> {
            // Log d all records
            for (Record record : records) {
                Log.d("RecordsFragment", "Record: " + record.getRecordId());
            }
            displayRecords(records);
            if (isFirst) {
                isFirst = false;
                if (!records.isEmpty()) {
                    Record topItem = records.get(0);
                    viewModel.syncById(topItem.getRecordId());
                } else {
                    viewModel.syncById(0);
                }
            }
        });

        viewModel.getSyncStatus().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                if (adapter.isEmpty()) {
                    loadingLayout.setVisibility(View.VISIBLE);
                    listRecord.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                }
            } else if (result instanceof Result.Success) {
                loadingLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                listRecord.setVisibility(View.VISIBLE);
            } else if (result instanceof Result.Error) {
                if (adapter.isEmpty()) {
                    loadingLayout.setVisibility(View.GONE);
                    listRecord.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorText.setText(((Result.Error<Boolean>) result).message);
                }
            }

        });
    }

    private void displayRecords(List<Record> records) {
        loadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        listRecord.setVisibility(View.VISIBLE);

        if (records != null) {
            adapter.clear();
            adapter.addAll(records);
            adapter.notifyDataSetChanged();
        }
    }

    private void onRecordClick(AdapterView<?> parent, View view, int position, long id) {
        Record clickedRecord = adapter.getItem(position);
        if (clickedRecord != null) {
            RecordDetailFragment detailFragment = RecordDetailFragment.newInstance(clickedRecord.getActivityType(), clickedRecord.getStartTime(), String.format(java.util.Locale.getDefault(), "%.2f km", clickedRecord.getDistance()), String.format(java.util.Locale.getDefault(), "%.1f km/h", clickedRecord.getSpeed()), String.format(java.util.Locale.getDefault(), "%.0f bpm", clickedRecord.getHeartRate()), String.format(java.util.Locale.getDefault(), "%.0f kcal", clickedRecord.getCalories()), clickedRecord.getDuration(), clickedRecord.getImageUrl());
            requireActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.slide_out_right).replace(getId(), detailFragment).addToBackStack(null).commit();

        }
    }

    private void setupScrollListener() {
        listRecord.setOnScrollListener(new android.widget.AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(android.widget.AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(android.widget.AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean isAtBottom = (firstVisibleItem + visibleItemCount) >= totalItemCount;
                if (isAtBottom && totalItemCount > 0) {
                    viewModel.loadMore(adapter.getCount());

                }
            }
        });
    }
}
