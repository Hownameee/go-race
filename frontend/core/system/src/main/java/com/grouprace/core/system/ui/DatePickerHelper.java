package com.grouprace.core.system.ui;

import android.app.DatePickerDialog;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Locale;

public final class DatePickerHelper {

    private DatePickerHelper() {}

    public static void attachDatePicker(@NonNull Fragment fragment, @NonNull EditText editText) {
        editText.setFocusable(false);
        editText.setClickable(true);
        editText.setLongClickable(false);
        editText.setOnClickListener(v -> showDatePicker(fragment, editText));
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker(fragment, editText);
            }
        });
    }

    private static void showDatePicker(@NonNull Fragment fragment, @NonNull EditText editText) {
        Calendar calendar = parseDateOrToday(editText.getText().toString().trim());

        DatePickerDialog dialog = new DatePickerDialog(
                fragment.requireContext(),
                (view, year, month, dayOfMonth) -> editText.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                ),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private static Calendar parseDateOrToday(String value) {
        Calendar calendar = Calendar.getInstance();

        if (value == null || value.isEmpty()) {
            return calendar;
        }

        String[] parts = value.split("-");
        if (parts.length != 3) {
            return calendar;
        }

        try {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1;
            int day = Integer.parseInt(parts[2]);
            calendar.set(year, month, day);
        } catch (NumberFormatException ignored) {
        }

        return calendar;
    }
}
