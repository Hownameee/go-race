package com.grouprace.core.system.ui;

import android.view.View;
import android.widget.TextView;

public final class ViewHelper {

    private ViewHelper() {}

    public static void bindOptionalView(View view, boolean shouldShow) {
        if (view == null) {
            return;
        }

        view.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
    }

    public static void bindOptionalText(TextView textView, String value) {
        if (textView == null) {
            return;
        }

        boolean hasValue = value != null && !value.trim().isEmpty();
        bindOptionalView(textView, hasValue);

        if (!hasValue) {
            textView.setText("");
            return;
        }

        textView.setText(value);
    }
}
