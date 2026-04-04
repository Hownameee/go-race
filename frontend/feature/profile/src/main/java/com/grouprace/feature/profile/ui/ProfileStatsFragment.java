package com.grouprace.feature.profile.ui;

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

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.WeeklyRecordPoint;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;
import com.grouprace.feature.profile.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileStatsFragment extends Fragment {
    private ProfileViewModel viewModel;
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

        viewModel = new ViewModelProvider(requireParentFragment()).get(ProfileViewModel.class);

        runStatsButton = view.findViewById(R.id.profile_run_stats_button);
        walkStatsButton = view.findViewById(R.id.profile_walk_stats_button);
        recordPeriod = view.findViewById(R.id.profile_record_period);
        recordDistance = view.findViewById(R.id.profile_record_distance);
        recordDuration = view.findViewById(R.id.profile_record_duration);
        recordElevation = view.findViewById(R.id.profile_record_elevation);
        recordChart = view.findViewById(R.id.profile_record_chart);

        runStatsButton.setOnClickListener(v -> viewModel.selectActivityType(ProfileViewModel.ACTIVITY_RUNNING));
        walkStatsButton.setOnClickListener(v -> viewModel.selectActivityType(ProfileViewModel.ACTIVITY_WALKING));

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
                return formatShortDate(points.get(index).getWeekStart());
            }
        });
        recordChart.invalidate();

        bindSelectedRecordPoint(points.get(points.size() - 1));
        recordChart.highlightValue(points.size() - 1, 0);
    }

    private void bindSelectedRecordPoint(@NonNull WeeklyRecordPoint point) {
        recordPeriod.setText(formatDateRange(point.getWeekStart(), point.getWeekEnd()));
        recordDistance.setText(new DecimalFormat("0.0").format(point.getTotalDistanceKm()) + " km");
        recordDuration.setText(formatDuration(point.getTotalDurationSeconds()));
        recordElevation.setText(new DecimalFormat("0").format(point.getTotalElevationGainM()) + " m");
    }

    private void clearWeeklySummary() {
        currentWeeklySummary = null;
        recordChart.clear();
        recordPeriod.setText("No period selected");
        recordDistance.setText("0.0 km");
        recordDuration.setText("0m");
        recordElevation.setText("0 m");
    }

    private String formatShortDate(String isoDate) {
        try {
            LocalDate date = LocalDate.parse(isoDate);
            return date.format(DateTimeFormatter.ofPattern("MM/dd", Locale.US));
        } catch (Exception ignored) {
            return isoDate;
        }
    }

    private String formatDateRange(String startIsoDate, String endIsoDate) {
        try {
            LocalDate start = LocalDate.parse(startIsoDate);
            LocalDate end = LocalDate.parse(endIsoDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.US);
            return start.format(formatter) + " - " + end.format(formatter);
        } catch (Exception ignored) {
            return startIsoDate + " - " + endIsoDate;
        }
    }

    private String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        return hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
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
