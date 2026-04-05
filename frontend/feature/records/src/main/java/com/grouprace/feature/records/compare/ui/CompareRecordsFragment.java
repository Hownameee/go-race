package com.grouprace.feature.records.compare.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.model.Record;
import com.grouprace.feature.records.R;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CompareRecordsFragment extends Fragment {

    // Định nghĩa màu sắc cho dễ quản lý
    private final int COLOR_WIN = Color.parseColor("#4CAF50"); // Xanh lá
    private final int COLOR_LOSE = Color.parseColor("#FF5252"); // Đỏ
    private final int COLOR_NEUTRAL = Color.parseColor("#FFFFFF"); // Trắng mặc định
    private CompareRecordsViewModel viewModel;
    private View btnSelectA, btnSelectB;
    private LinearLayout llComparisonGrid;
    private TextView tvTitleA, tvTitleB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compare_records, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CompareRecordsViewModel.class);

        btnSelectA = view.findViewById(R.id.btn_select_record_a);
        btnSelectB = view.findViewById(R.id.btn_select_record_b);
        llComparisonGrid = view.findViewById(R.id.ll_comparison_grid);
        tvTitleA = view.findViewById(R.id.tv_title_a);
        tvTitleB = view.findViewById(R.id.tv_title_b);

        btnSelectA.setOnClickListener(v -> showPicker(true));
        btnSelectB.setOnClickListener(v -> showPicker(false));

        observeViewModel();
    }

    private void showPicker(boolean isA) {
        RecordPickerDialog dialog = new RecordPickerDialog();
        dialog.setOnRecordSelectedListener(record -> {
            if (isA) {
                viewModel.setRecordA(record);
            } else {
                viewModel.setRecordB(record);
            }
        });
        dialog.show(getChildFragmentManager(), "record_picker");
    }

    private void observeViewModel() {
        viewModel.getRecordA().observe(getViewLifecycleOwner(), r -> updateUI());
        viewModel.getRecordB().observe(getViewLifecycleOwner(), r -> updateUI());
    }

    private void updateUI() {
        Record recordA = viewModel.getRecordA().getValue();
        Record recordB = viewModel.getRecordB().getValue();

        // 1. Cập nhật tiêu đề trên các Card chọn
        tvTitleA.setText(recordA != null ? recordA.getTitle() : "Select Record 1");
        tvTitleB.setText(recordB != null ? recordB.getTitle() : "Select Record 2");

        // 2. LOGIC INSTANT DISPLAY: Bật bảng lên ngay nếu có ít nhất 1 bản ghi
        if (recordA != null || recordB != null) {
            llComparisonGrid.setVisibility(View.VISIBLE);
            bindStats(recordA, recordB);
        } else {
            llComparisonGrid.setVisibility(View.GONE);
        }
    }

    private void bindStats(@Nullable Record a, @Nullable Record b) {
        boolean bothSelected = (a != null && b != null);

        // Activity Type (String so sánh kiểu bằng nhau)
        String typeA = a != null ? a.getActivityType() : "-";
        String typeB = b != null ? b.getActivityType() : "-";
        updateRow(getView().findViewById(R.id.row_activity_type), "Activity", typeA, typeB, 0);

        // Distance (Higher is better)
        double distA = a != null ? a.getDistance() : 0;
        double distB = b != null ? b.getDistance() : 0;
        int distWinner = bothSelected ? viewModel.compare(distA, distB, true) : 0;
        updateRow(getView().findViewById(R.id.row_distance), "Distance", a != null ? String.format(Locale.getDefault(), "%.2f km", distA) : "-", b != null ? String.format(Locale.getDefault(), "%.2f km", distB) : "-", distWinner);

        // Duration (Higher is better)
        int durA = a != null ? a.getDuration() : 0;
        int durB = b != null ? b.getDuration() : 0;
        int durWinner = bothSelected ? viewModel.compare(durA, durB, true) : 0;
        updateRow(getView().findViewById(R.id.row_duration), "Time", a != null ? TimeUtils.formatDuration(durA) : "-", b != null ? TimeUtils.formatDuration(durB) : "-", durWinner);

        // Speed (Higher is better)
        double speedA = a != null ? a.getSpeed() : 0;
        double speedB = b != null ? b.getSpeed() : 0;
        int speedWinner = bothSelected ? viewModel.compare(speedA, speedB, true) : 0;
        updateRow(getView().findViewById(R.id.row_speed), "Speed", a != null ? String.format(Locale.getDefault(), "%.1f km/h", speedA) : "-", b != null ? String.format(Locale.getDefault(), "%.1f km/h", speedB) : "-", speedWinner);

        // Calories (Higher is better)
        double calA = a != null ? a.getCalories() : 0;
        double calB = b != null ? b.getCalories() : 0;
        int calWinner = bothSelected ? viewModel.compare(calA, calB, true) : 0;
        updateRow(getView().findViewById(R.id.row_calories), "Calories", a != null ? String.format(Locale.getDefault(), "%.0f kcal", calA) : "-", b != null ? String.format(Locale.getDefault(), "%.0f kcal", calB) : "-", calWinner);

        // Heart Rate (Neutral, 0)
        double hrA = a != null ? a.getHeartRate() : 0;
        double hrB = b != null ? b.getHeartRate() : 0;
        updateRow(getView().findViewById(R.id.row_heart_rate), "Avg Heart Rate", a != null ? String.format(Locale.getDefault(), "%.0f bpm", hrA) : "-", b != null ? String.format(Locale.getDefault(), "%.0f bpm", hrB) : "-", 0);

        ImageView ivImageA = getView().findViewById(R.id.iv_record_a_image);
        ImageView ivImageB = getView().findViewById(R.id.iv_record_b_image);

// Load ảnh A
        if (a != null && a.getImageUrl() != null) {
            Glide.with(this).load(a.getImageUrl()).into(ivImageA);
        } else {
            ivImageA.setImageDrawable(null); // Hoặc set ảnh mặc định
        }

// Load ảnh B
        if (b != null && b.getImageUrl() != null) {
            Glide.with(this).load(b.getImageUrl()).into(ivImageB);
        } else {
            ivImageB.setImageDrawable(null);
        }
    }

    private void updateRow(View row, String label, String valA, String valB, int winner) {
        TextView tvLabel = row.findViewById(R.id.tv_label);
        TextView tvA = row.findViewById(R.id.tv_value_a);
        TextView tvB = row.findViewById(R.id.tv_value_b);

        tvLabel.setText(label);
        tvA.setText(valA);
        tvB.setText(valB);

        // Áp dụng màu sắc thông minh
        tvA.setTextColor(winner == 1 ? COLOR_WIN : (winner == -1 ? COLOR_LOSE : COLOR_NEUTRAL));
        tvB.setTextColor(winner == -1 ? COLOR_WIN : (winner == 1 ? COLOR_LOSE : COLOR_NEUTRAL));
    }
}