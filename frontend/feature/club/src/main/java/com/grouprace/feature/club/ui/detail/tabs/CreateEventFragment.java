package com.grouprace.feature.club.ui.detail.tabs;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.club.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateEventFragment extends Fragment {

    private static final String ARG_CLUB_ID = "CLUB_ID";
    private CreateEventViewModel viewModel;
    private int clubId;

    public CreateEventFragment() {
        super(R.layout.fragment_create_event);
    }

    public static CreateEventFragment newInstance(int clubId) {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLUB_ID, clubId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clubId = getArguments() != null ? getArguments().getInt(ARG_CLUB_ID, -1) : -1;
        if (clubId == -1) return;

        viewModel = new ViewModelProvider(this).get(CreateEventViewModel.class);

        TopAppBarHelper.setupTopAppBar(view, new TopAppBarConfig.Builder()
                .setTitle("Create Event")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back)
                .setOnLeftIconClick(v -> requireActivity().getSupportFragmentManager().popBackStack())
                .build());

        TextInputEditText etTitle = view.findViewById(R.id.et_event_title);
        TextInputEditText etDesc = view.findViewById(R.id.et_event_desc);
        TextInputEditText etTargetDist = view.findViewById(R.id.et_target_distance);
        TextInputEditText etTargetDur = view.findViewById(R.id.et_target_duration);
        TextInputEditText etStartDate = view.findViewById(R.id.et_start_date);
        TextInputEditText etEndDate = view.findViewById(R.id.et_end_date);
        Button btnCreate = view.findViewById(R.id.btn_create_event);
        ProgressBar pbCreate = view.findViewById(R.id.pb_create_event);

        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate, isoFormat, displayFormat));
        etEndDate.setOnClickListener(v -> showDatePicker(etEndDate, isoFormat, displayFormat));

        btnCreate.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String targetDistStr = etTargetDist.getText().toString().trim();
            String targetDurStr = etTargetDur.getText().toString().trim();
            String startDate = (String) etStartDate.getTag();
            String endDate = (String) etEndDate.getTag();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(getContext(), "Title is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(startDate)) {
                Toast.makeText(getContext(), "Start Date is required", Toast.LENGTH_SHORT).show();
                return;
            }

            double targetDist = 0;
            if (TextUtils.isEmpty(targetDistStr)) {
                Toast.makeText(getContext(), "Target distance is required", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                targetDist = Double.parseDouble(targetDistStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid target distance", Toast.LENGTH_SHORT).show();
                return;
            }

            int targetDurSeconds = 0;
            if (TextUtils.isEmpty(targetDurStr)) {
                Toast.makeText(getContext(), "Target duration is required", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                targetDurSeconds = Integer.parseInt(targetDurStr) * 60;
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid target duration", Toast.LENGTH_SHORT).show();
                return;
            }

            if (targetDist <= 0 || targetDurSeconds <= 0) {
                Toast.makeText(getContext(), "Target values must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            pbCreate.setVisibility(View.VISIBLE);
            btnCreate.setEnabled(false);

            viewModel.createEvent(clubId, title, desc, targetDist, targetDurSeconds, startDate, endDate).observe(getViewLifecycleOwner(), result -> {
                pbCreate.setVisibility(View.GONE);
                btnCreate.setEnabled(true);
                
                if (result instanceof Result.Success) {
                    Toast.makeText(getContext(), "Event created successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else if (result instanceof Result.Error) {
                    Toast.makeText(getContext(), ((Result.Error<?>) result).message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void showDatePicker(TextInputEditText editText, SimpleDateFormat isoFormat, SimpleDateFormat displayFormat) {
        com.google.android.material.datepicker.MaterialDatePicker<Long> datePicker = 
            com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(com.google.android.material.datepicker.MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date(selection);
            editText.setText(displayFormat.format(date));
            editText.setTag(isoFormat.format(date));
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }
}
