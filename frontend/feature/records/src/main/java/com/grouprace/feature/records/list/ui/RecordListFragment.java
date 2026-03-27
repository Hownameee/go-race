package com.grouprace.feature.records.list.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
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
import com.grouprace.core.system.component.LoadingButton;
import com.grouprace.feature.records.R;
import com.grouprace.feature.records.detail.ui.RecordDetailFragment;
import com.grouprace.feature.records.list.ui.adapter.RecordAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for displaying a paginated list of records.
 * Supports refreshing, loading more data, and handling error states.
 */
@AndroidEntryPoint
public class RecordListFragment extends Fragment {
    private RecordListViewModel viewModel;
    private RecordAdapter adapter;
    private View loadMoreLoading;
    private View loadMoreError;
    private LoadingButton retryBtn;

    public RecordListFragment() {
        super(R.layout.fragment_list_record);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        ListView listRecord = view.findViewById(R.id.lv_record_list);
        LinearLayout loadingLayout = view.findViewById(R.id.ll_loading);
        LinearLayout errorLayout = view.findViewById(R.id.ll_error);
        retryBtn = view.findViewById(R.id.btn_retry);
        TextView errorText = view.findViewById(R.id.tv_error_message);

        setupFooter(listRecord);
        setupAdapter(listRecord);

        viewModel = new ViewModelProvider(this).get(RecordListViewModel.class);
        observeViewModel(loadingLayout, errorLayout, errorText, listRecord);

        viewModel.initialLoad();

        retryBtn.setOnClickListener(v -> viewModel.refresh());
        setupScrollListener(listRecord);
    }

    private void setupFooter(ListView listRecord) {
        View loadMoreFooter = LayoutInflater.from(requireContext()).inflate(R.layout.footer_load_more, listRecord, false);
        loadMoreLoading = loadMoreFooter.findViewById(R.id.ll_load_more_loading);
        loadMoreError = loadMoreFooter.findViewById(R.id.ll_load_more_error);
        LoadingButton loadMoreRetryBtn = loadMoreFooter.findViewById(R.id.btn_load_more_retry);

        loadMoreRetryBtn.setOnClickListener(v -> viewModel.loadMore());
        listRecord.addFooterView(loadMoreFooter, null, false);
        setLoadMoreVisibility(false, false);
    }
    
    private void setupAdapter(ListView listRecord) {
        adapter = new RecordAdapter(requireContext(), new ArrayList<>());
        listRecord.setAdapter(adapter);
        listRecord.setOnItemClickListener(this::onRecordClick);
    }

    private void observeViewModel(View loadingLayout, View errorLayout, TextView errorText, View listRecord) {
        viewModel.getRecords().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                handleLoadingState(loadingLayout, errorLayout);
            } else if (result instanceof Result.Success) {
                handleSuccessState(((Result.Success<List<Record>>) result).data, loadingLayout, errorLayout, listRecord);
            } else if (result instanceof Result.Error) {
                handleErrorState(((Result.Error<?>) result).message, loadingLayout, errorLayout, errorText, listRecord);
            }
        });
    }

    private void handleLoadingState(View loadingLayout, View errorLayout) {
        if (adapter.getCount() == 0) {
            loadingLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        } else {
            setLoadMoreVisibility(true, false);
        }
    }

    private void handleSuccessState(List<Record> records, View loadingLayout, View errorLayout, View listRecord) {
        loadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        setLoadMoreVisibility(false, false);
        listRecord.setVisibility(View.VISIBLE);

        if (records != null) {
            adapter.clear();
            adapter.addAll(records);
            adapter.notifyDataSetChanged();
        }
    }

    private void handleErrorState(String message, View loadingLayout, View errorLayout, TextView errorText, View listRecord) {
        loadingLayout.setVisibility(View.GONE);
        if (adapter.getCount() == 0) {
            errorText.setText(message != null ? message : "An unknown error occurred");
            errorLayout.setVisibility(View.VISIBLE);
            listRecord.setVisibility(View.GONE);
            setLoadMoreVisibility(false, false);
        } else {
            setLoadMoreVisibility(false, true);
        }
    }

    private void onRecordClick(AdapterView<?> parent, View view, int position, long id) {
        Record clickedRecord = adapter.getItem(position);
        if (clickedRecord != null) {
            RecordDetailFragment detailFragment = RecordDetailFragment.newInstance(clickedRecord.getActivityType(), clickedRecord.getStartTime(), String.format(java.util.Locale.getDefault(), "%.2f km", clickedRecord.getDistance()), String.format(java.util.Locale.getDefault(), "%.1f km/h", clickedRecord.getSpeed()), String.format(java.util.Locale.getDefault(), "%.0f bpm", clickedRecord.getHeartRate()), String.format(java.util.Locale.getDefault(), "%.0f kcal", clickedRecord.getCalories()), clickedRecord.getDuration(), clickedRecord.getImageUrl());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.slide_out_right).replace(getId(), detailFragment).addToBackStack(null).commit();

        }
    }

    private String formatValue(float value, String unit) {
        return String.format(Locale.getDefault(), "%.1f %s", value, unit);
    }

    private void setupScrollListener(ListView listRecord) {
        listRecord.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!viewModel.getIsLast() && (firstVisibleItem + visibleItemCount) >= totalItemCount && totalItemCount > 1 && !(viewModel.getRecords().getValue() instanceof Result.Loading) && loadMoreError.getVisibility() != View.VISIBLE) {
                    viewModel.loadMore();
                }
            }
        });
    }

    private void setLoadMoreVisibility(boolean isLoading, boolean isError) {
        if (loadMoreLoading != null)
            loadMoreLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (loadMoreError != null) loadMoreError.setVisibility(isError ? View.VISIBLE : View.GONE);
    }
}
