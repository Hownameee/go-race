package com.grouprace.feature.records.compare.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.model.Record;
import com.grouprace.feature.records.R;

import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecordPickerDialog extends DialogFragment {

    public interface OnRecordSelectedListener {
        void onRecordSelected(Record record);
    }

    private OnRecordSelectedListener listener;
    private RecordPickerViewModel viewModel;

    public void setOnRecordSelectedListener(OnRecordSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Pick a Record");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_record_picker, null);
        ListView listView = view.findViewById(R.id.lv_picker);

        viewModel = new ViewModelProvider(this).get(RecordPickerViewModel.class);
        viewModel.sync();

        viewModel.getRecords().observe(this, records -> {
            List<String> titles = records.stream()
                    .map(r -> r.getTitle() + " (" + r.getStartTime() + ")")
                    .collect(Collectors.toList());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, titles);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, v, position, id) -> {
                if (listener != null) {
                    listener.onRecordSelected(records.get(position));
                }
                dismiss();
            });
        });

        builder.setView(view);
        builder.setNegativeButton("Cancel", (dialog, which) -> dismiss());

        return builder.create();
    }
}
