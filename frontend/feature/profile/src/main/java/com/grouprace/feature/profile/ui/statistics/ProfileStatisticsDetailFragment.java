package com.grouprace.feature.profile.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.core.network.model.record.RecordStatisticsBucketResponse;
import com.grouprace.feature.profile.R;
import com.grouprace.feature.profile.ui.main.ProfileActivityType;
import com.grouprace.feature.profile.util.ProfileFormatUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileStatisticsDetailFragment extends Fragment {
    private static final String ARG_IS_SELF = "arg_is_self";
    private static final String ARG_USER_ID = "arg_user_id";

    private ProfileStatisticsDetailViewModel viewModel;
    private ImageButton backButton;
    private Button runButton;
    private Button walkButton;
    private TextView loadingState;
    private TextView errorState;
    private View contentContainer;
    private TextView weeklyAverageActivityLabel;
    private TextView weeklyAverageActivityValue;
    private TextView weeklyAverageTimeValue;
    private TextView weeklyAverageDistanceValue;
    private TextView ytdActivityLabel;
    private TextView ytdActivityValue;
    private TextView ytdTimeValue;
    private TextView ytdDistanceValue;
    private TextView allTimeActivityLabel;
    private TextView allTimeActivityValue;
    private TextView allTimeDistanceValue;

    public static ProfileStatisticsDetailFragment newInstance(boolean isSelf, int userId) {
        ProfileStatisticsDetailFragment fragment = new ProfileStatisticsDetailFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_SELF, isSelf);
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_statistics_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean isSelf = getArguments() == null || getArguments().getBoolean(ARG_IS_SELF, true);
        int userId = getArguments() == null ? -1 : getArguments().getInt(ARG_USER_ID, -1);
        viewModel = new ViewModelProvider(this).get(ProfileStatisticsDetailViewModel.class);
        viewModel.initialize(isSelf, userId);

        backButton = view.findViewById(R.id.profile_statistics_back_button);
        runButton = view.findViewById(R.id.profile_statistics_run_button);
        walkButton = view.findViewById(R.id.profile_statistics_walk_button);
        loadingState = view.findViewById(R.id.profile_statistics_loading_state);
        errorState = view.findViewById(R.id.profile_statistics_error_state);
        contentContainer = view.findViewById(R.id.profile_statistics_content_container);
        weeklyAverageActivityLabel = view.findViewById(R.id.profile_statistics_weekly_average_activity_label);
        weeklyAverageActivityValue = view.findViewById(R.id.profile_statistics_weekly_average_activity_value);
        weeklyAverageTimeValue = view.findViewById(R.id.profile_statistics_weekly_average_time_value);
        weeklyAverageDistanceValue = view.findViewById(R.id.profile_statistics_weekly_average_distance_value);
        ytdActivityLabel = view.findViewById(R.id.profile_statistics_ytd_activity_label);
        ytdActivityValue = view.findViewById(R.id.profile_statistics_ytd_activity_value);
        ytdTimeValue = view.findViewById(R.id.profile_statistics_ytd_time_value);
        ytdDistanceValue = view.findViewById(R.id.profile_statistics_ytd_distance_value);
        allTimeActivityLabel = view.findViewById(R.id.profile_statistics_all_time_activity_label);
        allTimeActivityValue = view.findViewById(R.id.profile_statistics_all_time_activity_value);
        allTimeDistanceValue = view.findViewById(R.id.profile_statistics_all_time_distance_value);

        backButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        runButton.setOnClickListener(v -> viewModel.selectActivityType(ProfileActivityType.RUNNING));
        walkButton.setOnClickListener(v -> viewModel.selectActivityType(ProfileActivityType.WALKING));

        observeActivityType();
        observeStatistics();
    }

    private void observeActivityType() {
        viewModel.getSelectedActivityType().observe(getViewLifecycleOwner(), activityType -> {
            boolean isRunning = ProfileActivityType.RUNNING.equals(activityType);
            updateActivityButtonState(runButton, isRunning);
            updateActivityButtonState(walkButton, !isRunning);
            String plural = isRunning ? "runs" : "walks";

            weeklyAverageActivityLabel.setText("Average " + plural + " per week");
            ytdActivityLabel.setText("Total " + plural);
            allTimeActivityLabel.setText("Total " + plural);
        });
    }

    private void observeStatistics() {
        viewModel.getStatistics().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                loadingState.setVisibility(View.VISIBLE);
                errorState.setVisibility(View.GONE);
                contentContainer.setVisibility(View.GONE);
            } else if (result instanceof Result.Success) {
                loadingState.setVisibility(View.GONE);
                errorState.setVisibility(View.GONE);
                contentContainer.setVisibility(View.VISIBLE);
                bindStatistics(((Result.Success<RecordProfileStatisticsResponse>) result).data);
            } else if (result instanceof Result.Error) {
                loadingState.setVisibility(View.GONE);
                contentContainer.setVisibility(View.GONE);
                errorState.setVisibility(View.VISIBLE);
                String message = ((Result.Error<RecordProfileStatisticsResponse>) result).message;
                errorState.setText(message != null ? message : "Unable to load statistics.");
            }
        });
    }

    private void bindStatistics(@Nullable RecordProfileStatisticsResponse response) {
        if (response == null) {
            return;
        }

        bindWeeklyAverage(response.getWeeklyAverage());
        bindYearToDate(response.getYearToDate());
        bindAllTime(response.getAllTime());
    }

    private void bindWeeklyAverage(@Nullable RecordStatisticsBucketResponse bucket) {
        weeklyAverageActivityValue.setText(ProfileFormatUtils.formatActivityCount(bucket != null ? bucket.getTotalActivities() : 0));
        weeklyAverageTimeValue.setText(TimeUtils.formatDuration(bucket != null ? bucket.getTotalDurationSeconds() : 0));
        weeklyAverageDistanceValue.setText(ProfileFormatUtils.formatDistance(bucket != null ? bucket.getTotalDistanceKm() : 0));
    }

    private void bindYearToDate(@Nullable RecordStatisticsBucketResponse bucket) {
        ytdActivityValue.setText(ProfileFormatUtils.formatActivityCount(bucket != null ? bucket.getTotalActivities() : 0));
        ytdTimeValue.setText(TimeUtils.formatDuration(bucket != null ? bucket.getTotalDurationSeconds() : 0));
        ytdDistanceValue.setText(ProfileFormatUtils.formatDistance(bucket != null ? bucket.getTotalDistanceKm() : 0));
    }

    private void bindAllTime(@Nullable RecordStatisticsBucketResponse bucket) {
        allTimeActivityValue.setText(ProfileFormatUtils.formatActivityCount(bucket != null ? bucket.getTotalActivities() : 0));
        allTimeDistanceValue.setText(ProfileFormatUtils.formatDistance(bucket != null ? bucket.getTotalDistanceKm() : 0));
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
