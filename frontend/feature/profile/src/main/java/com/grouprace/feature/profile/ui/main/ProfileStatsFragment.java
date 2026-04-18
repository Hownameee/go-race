package com.grouprace.feature.profile.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.WeeklyRecordPoint;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;
import com.grouprace.feature.profile.R;
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

public class ProfileStatsFragment extends Fragment {
    private ProfileStatsOwner statsOwner;
    private Button runStatsButton;
    private Button walkStatsButton;
    private TextView recordPeriod;
    private TextView recordDistance;
    private TextView recordDuration;
    private TextView recordElevation;
    private LineChart recordChart;
    private WeeklyRecordSummary currentWeeklySummary;

    public static ProfileStatsFragment newInstance() {
        return new ProfileStatsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Fragment parentFragment = requireParentFragment();
        if (!(parentFragment instanceof ProfileStatsOwner)) {
            throw new IllegalStateException("Parent fragment must implement ProfileStatsOwner");
        }
        statsOwner = (ProfileStatsOwner) parentFragment;

        runStatsButton = view.findViewById(R.id.profile_run_stats_button);
        walkStatsButton = view.findViewById(R.id.profile_walk_stats_button);
        recordPeriod = view.findViewById(R.id.profile_record_period);
        recordDistance = view.findViewById(R.id.profile_record_distance);
        recordDuration = view.findViewById(R.id.profile_record_duration);
        recordElevation = view.findViewById(R.id.profile_record_elevation);
        recordChart = view.findViewById(R.id.profile_record_chart);

        runStatsButton.setOnClickListener(v -> statsOwner.onSelectActivityType(ProfileViewModel.ACTIVITY_RUNNING));
        walkStatsButton.setOnClickListener(v -> statsOwner.onSelectActivityType(ProfileViewModel.ACTIVITY_WALKING));

        setupRecordChart();
        observeWeeklySummary();
        observeActivitySelection();
    }

    private void observeWeeklySummary() {
        statsOwner.getWeeklySummaryLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                currentWeeklySummary = ((Result.Success<WeeklyRecordSummary>) result).data;
                bindWeeklySummary(currentWeeklySummary);
            } else if (result instanceof Result.Error) {
                clearWeeklySummary();
            }
        });
    }

    private void observeActivitySelection() {
        statsOwner.getSelectedActivityTypeLiveData().observe(getViewLifecycleOwner(), activityType -> {
            boolean isRunning = ProfileViewModel.ACTIVITY_RUNNING.equals(activityType);
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
            public void onValueSelected(Entry e, Highlight h) {
                if (currentWeeklySummary == null || currentWeeklySummary.getPoints() == null) return;
                int index = Math.round(e.getX());
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
                if (index < 0 || index >= points.size()) return "";
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
        recordElevation.setText(ProfileFormatUtils.formatElevation(point.getTotalElevationGainM()));
    }

    private void clearWeeklySummary() {
        currentWeeklySummary = null;
        recordChart.clear();
        recordPeriod.setText("No period selected");
        recordDistance.setText("0.0 km");
        recordDuration.setText("0m");
        recordElevation.setText("0 m");
    }

    private int findLatestNonZeroPointIndex(@NonNull List<WeeklyRecordPoint> points) {
        for (int index = points.size() - 1; index >= 0; index--) {
            WeeklyRecordPoint point = points.get(index);
            if (point.getTotalDistanceKm() > 0
                    || point.getTotalDurationSeconds() > 0
                    || point.getTotalElevationGainM() > 0) {
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
