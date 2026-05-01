package com.grouprace.feature.profile.ui.main.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.WeeklyRecordPoint;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;
import com.grouprace.feature.profile.R;
import com.grouprace.feature.profile.ui.main.ProfileActivityType;
import com.grouprace.feature.profile.util.ProfileFormatUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileStatsFragment extends Fragment {
    private static final String ARG_USER_ID = "arg_user_id";
    private static final String ARG_IS_SELF = "arg_is_self";

    private ProfileStatsViewModel viewModel;
    private Button runStatsButton;
    private Button walkStatsButton;
    private TextView recordPeriod;
    private TextView recordDistance;
    private TextView recordDuration;
    private LineChart recordChart;
    private WeeklyRecordSummary currentWeeklySummary;

    public static ProfileStatsFragment newInstance(int userId, boolean isSelf) {
        ProfileStatsFragment fragment = new ProfileStatsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        args.putBoolean(ARG_IS_SELF, isSelf);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int userId = getArguments() != null ? getArguments().getInt(ARG_USER_ID, -1) : -1;
        boolean isSelf = getArguments() == null || getArguments().getBoolean(ARG_IS_SELF, true);
        viewModel = new ViewModelProvider(this).get(ProfileStatsViewModel.class);
        viewModel.initialize(userId, isSelf);

        runStatsButton = view.findViewById(R.id.profile_run_stats_button);
        walkStatsButton = view.findViewById(R.id.profile_walk_stats_button);
        recordPeriod = view.findViewById(R.id.profile_record_period);
        recordDistance = view.findViewById(R.id.profile_record_distance);
        recordDuration = view.findViewById(R.id.profile_record_duration);
        recordChart = view.findViewById(R.id.profile_record_chart);

        runStatsButton.setOnClickListener(v -> viewModel.selectActivityType(ProfileActivityType.RUNNING));
        walkStatsButton.setOnClickListener(v -> viewModel.selectActivityType(ProfileActivityType.WALKING));

        setupRecordChart();
        observeWeeklySummary();
        observeActivitySelection();
    }

    private void observeWeeklySummary() {
        viewModel.getWeeklySummary().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                currentWeeklySummary = ((Result.Success<WeeklyRecordSummary>) result).data;
                bindWeeklySummary(currentWeeklySummary);
            } else if (result instanceof Result.Error) {
                clearWeeklySummary();
            }
        });
    }

    private void observeActivitySelection() {
        viewModel.getSelectedActivityType().observe(getViewLifecycleOwner(), activityType -> {
            boolean isRunning = ProfileActivityType.RUNNING.equals(activityType);
            updateActivityButtonState(runStatsButton, isRunning);
            updateActivityButtonState(walkStatsButton, !isRunning);
        });
    }

    private void setupRecordChart() {
        recordChart.getDescription().setEnabled(false);
        recordChart.getLegend().setEnabled(false);
        recordChart.setTouchEnabled(true);
        recordChart.setDragEnabled(false);
        recordChart.setScaleEnabled(false);
        recordChart.setPinchZoom(false);
        recordChart.setNoDataText("No weekly records yet");
        recordChart.getAxisRight().setEnabled(false);

        XAxis xAxis = recordChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(requireContext().getColor(com.grouprace.core.system.R.color.text_secondary));

        recordChart.getAxisLeft().setAxisMinimum(0f);
        recordChart.getAxisLeft().setTextColor(requireContext().getColor(com.grouprace.core.system.R.color.text_secondary));

        recordChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                if (currentWeeklySummary == null || currentWeeklySummary.getPoints() == null) {
                    return;
                }
                int index = Math.round(entry.getX());
                if (index >= 0 && index < currentWeeklySummary.getPoints().size()) {
                    bindSelectedRecordPoint(currentWeeklySummary.getPoints().get(index));
                }
            }

            @Override
            public void onNothingSelected() {
            }
        });
    }

    private void bindWeeklySummary(@Nullable WeeklyRecordSummary summary) {
        if (summary == null || summary.getPoints() == null || summary.getPoints().isEmpty()) {
            clearWeeklySummary();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<WeeklyRecordPoint> points = summary.getPoints();
        for (int index = 0; index < points.size(); index++) {
            entries.add(new Entry(index, (float) points.get(index).getTotalDistanceKm()));
        }

        LineDataSet dataSet = new LineDataSet(entries, summary.getActivityType());
        dataSet.setColor(requireContext().getColor(com.grouprace.core.system.R.color.text_primary));
        dataSet.setCircleColor(requireContext().getColor(com.grouprace.core.system.R.color.text_primary));
        dataSet.setCircleHoleColor(requireContext().getColor(android.R.color.black));
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setMode(LineDataSet.Mode.LINEAR);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        recordChart.setData(new LineData(dataSets));
        recordChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value);
                if (index < 0 || index >= points.size()) {
                    return "";
                }
                return ProfileFormatUtils.formatShortDate(points.get(index).getWeekStart());
            }
        });
        recordChart.invalidate();

        int selectedIndex = findLatestNonZeroPointIndex(points);
        bindSelectedRecordPoint(points.get(selectedIndex));
        recordChart.highlightValue(selectedIndex, 0);
    }

    private void bindSelectedRecordPoint(@NonNull WeeklyRecordPoint point) {
        recordPeriod.setText(ProfileFormatUtils.formatDateRange(point.getWeekStart(), point.getWeekEnd()));
        recordDistance.setText(ProfileFormatUtils.formatDistance(point.getTotalDistanceKm()));
        recordDuration.setText(TimeUtils.formatDuration(point.getTotalDurationSeconds()));
    }

    private void clearWeeklySummary() {
        currentWeeklySummary = null;
        recordChart.clear();
        recordPeriod.setText("No period selected");
        recordDistance.setText("0.0 km");
        recordDuration.setText("0m");
    }

    private int findLatestNonZeroPointIndex(@NonNull List<WeeklyRecordPoint> points) {
        for (int index = points.size() - 1; index >= 0; index--) {
            WeeklyRecordPoint point = points.get(index);
            if (point.getTotalDistanceKm() > 0 || point.getTotalDurationSeconds() > 0) {
                return index;
            }
        }
        return points.size() - 1;
    }

    private void updateActivityButtonState(@NonNull Button button, boolean isSelected) {
        int backgroundRes = isSelected
                ? com.grouprace.core.system.R.drawable.bg_button_rounded
                : com.grouprace.core.system.R.drawable.bg_button_secondary_rounded;
        int textColor = requireContext().getColor(android.R.color.black);

        button.setBackgroundResource(backgroundRes);
        button.setTextColor(textColor);
        button.setEnabled(!isSelected);
        button.setAlpha(1f);
    }
}
