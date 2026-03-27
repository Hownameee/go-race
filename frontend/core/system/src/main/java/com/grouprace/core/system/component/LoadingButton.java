package com.grouprace.core.system.component;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.grouprace.core.system.R;

public class LoadingButton extends FrameLayout {
    private MaterialButton button;
    private ProgressBar progressBar;
    private ColorStateList originalTextColor;

    public LoadingButton(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.loading_button, this, true);

        button = findViewById(R.id.btn_action);
        progressBar = findViewById(R.id.progress_bar);

        originalTextColor = button.getTextColors();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton);
            try {
                String text = a.getString(R.styleable.LoadingButton_buttonText);
                boolean isLoading = a.getBoolean(R.styleable.LoadingButton_isLoading, false);

                if (text != null) {
                    button.setText(text);
                }

                setLoading(isLoading);
            } finally {
                a.recycle();
            }
        }
    }

    public void setLoading(boolean isLoading) {
        if (isLoading) {
            button.setTextColor(Color.TRANSPARENT);
            button.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            button.setTextColor(originalTextColor);
            button.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setText(String text) {
        button.setText(text);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        button.setOnClickListener(l);
    }
}