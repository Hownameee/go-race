package com.grouprace.feature.posts.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.grouprace.feature.posts.R;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerBottomSheet extends BottomSheetDialogFragment {

    public interface OnColorChangeListener {
        void onColorChanged(int color);
    }

    public interface OnBackgroundChangeListener {
        void onBackgroundChanged(int colorWithAlpha);
    }

    private OnColorChangeListener changeListener;
    private OnBackgroundChangeListener bgChangeListener;
    private Runnable onDismissListener;

    private int currentMode = -1; // -1 to indicate uninitialized state

    // Text Mode State
    private int customColorText = Color.WHITE;
    private float[] hsvText = new float[3];
    private View selectedSwatchText;

    // Background Mode State
    private int customColorBg = 0x80000000;
    private float[] hsvBg = new float[3];
    private int alphaBg = 128; // 0-255
    private View selectedSwatchBg;

    // Shared Working State 
    private float[] currentHsv = new float[3];
    private int currentAlpha = 255;
    private int currentCustomColor;

    private View swatchCustom;
    private FrameLayout flCustomSwatch;
    private SeekBar seekbarHue, seekbarSaturation, seekbarBrightness, seekbarOpacity;
    private View llOpacityContainer;
    private LinearLayout llSwatches;
    private com.google.android.material.tabs.TabLayout tabLayout;
    private final List<View> swatchViews = new ArrayList<>();

    private static final int[] TEXT_PRESETS = {
            Color.WHITE, Color.BLACK, Color.YELLOW, Color.CYAN,
            Color.RED, Color.GREEN, Color.MAGENTA, Color.rgb(255, 165, 0)
    };

    private static final int[] BG_PRESETS = {
            0x80000000, 0x40000000, 0xBF000000, 0x40FFFFFF,
            0x80FFFFFF, 0x40333333, 0x80333333, 0x801A237E
    };

    public static ColorPickerBottomSheet newInstance(int initialTextColor, int initialBgColor) {
        ColorPickerBottomSheet fragment = new ColorPickerBottomSheet();
        Bundle args = new Bundle();
        args.putInt("initial_text_color", initialTextColor);
        args.putInt("initial_bg_color", initialBgColor);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnBackgroundChangeListener(OnBackgroundChangeListener listener) {
        this.bgChangeListener = listener;
    }

    public void setOnColorChangeListener(OnColorChangeListener listener) {
        this.changeListener = listener;
    }

    public void setOnDismissListener(Runnable listener) {
        this.onDismissListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_color_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            customColorText = getArguments().getInt("initial_text_color");
            customColorBg = getArguments().getInt("initial_bg_color");
        }

        // Initialize Text HSV
        Color.colorToHSV(customColorText, hsvText);
        
        // Initialize Bg HSV and Alpha
        Color.colorToHSV(customColorBg, hsvBg);
        alphaBg = Color.alpha(customColorBg);

        swatchCustom = view.findViewById(R.id.swatch_custom);
        flCustomSwatch = view.findViewById(R.id.fl_custom_swatch);
        llSwatches = view.findViewById(R.id.ll_swatches);
        seekbarHue = view.findViewById(R.id.seekbar_hue);
        seekbarSaturation = view.findViewById(R.id.seekbar_saturation);
        seekbarBrightness = view.findViewById(R.id.seekbar_brightness);
        seekbarOpacity = view.findViewById(R.id.seekbar_opacity);
        llOpacityContainer = view.findViewById(R.id.ll_opacity_container);
        tabLayout = view.findViewById(R.id.tab_layout_mode);

        tabLayout.addTab(tabLayout.newTab().setText("Text"));
        tabLayout.addTab(tabLayout.newTab().setText("Background"));

        setupSliders();
        
        tabLayout.addOnTabSelectedListener(new com.google.android.material.tabs.TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(com.google.android.material.tabs.TabLayout.Tab tab) {
                switchMode(tab.getPosition());
            }
            @Override public void onTabUnselected(com.google.android.material.tabs.TabLayout.Tab tab) {}
            @Override public void onTabReselected(com.google.android.material.tabs.TabLayout.Tab tab) {}
        });

        switchMode(0);
    }

    private void switchMode(int mode) {
        if (currentMode == 0) {
            hsvText = currentHsv.clone();
            customColorText = currentCustomColor;
        } else if (currentMode == 1) {
            hsvBg = currentHsv.clone();
            alphaBg = currentAlpha;
            customColorBg = currentCustomColor;
        }

        currentMode = mode;

        // Load new mode state
        if (mode == 0) {
            currentHsv = hsvText.clone();
            currentAlpha = 255;
            currentCustomColor = customColorText;
            llOpacityContainer.setVisibility(View.INVISIBLE);
            setupSwatches(TEXT_PRESETS);
        } else {
            currentHsv = hsvBg.clone();
            currentAlpha = alphaBg;
            currentCustomColor = customColorBg;
            llOpacityContainer.setVisibility(View.VISIBLE);
            seekbarOpacity.setProgress((int) (currentAlpha / 2.55f));
            setupSwatches(BG_PRESETS);
        }

        updateUI();
    }

    private void updateUI() {
        updateSliders();
        updateCustomSwatch();

        // Restore selection
        View toSelect = (currentMode == 0) ? selectedSwatchText : selectedSwatchBg;
        if (toSelect != null) {
            selectSwatch(toSelect);
        } else {
            // Find match in presets
            int targetColor = (currentMode == 0) ? currentCustomColor : currentCustomColor;
            updateUIFromPresetMatch(targetColor);
        }
    }

    private void updateUIFromPresetMatch(int color) {
        int[] presets = (currentMode == 0) ? TEXT_PRESETS : BG_PRESETS;
        boolean found = false;
        for (int i = 0; i < presets.length; i++) {
            if (presets[i] == color) {
                selectSwatch(swatchViews.get(i + 1));
                found = true;
                break;
            }
        }
        if (!found) {
            selectSwatch(swatchCustom);
        }
        updateSeekBarGradients();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setDimAmount(0f);
            }
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.run();
        }
    }

    private void setupSwatches(int[] presets) {
        llSwatches.removeAllViews();
        swatchViews.clear();
        
        llSwatches.addView(flCustomSwatch);
        swatchViews.add(swatchCustom);

        for (int color : presets) {
            View swatch = createSwatchView(color);
            llSwatches.addView(swatch);
            swatchViews.add(swatch);
        }
        
        flCustomSwatch.setOnClickListener(v -> {
            selectSwatch(swatchCustom);
        });
    }

    private View createSwatchView(int color) {
        View swatch = new View(getContext());
        int size = (int) (44 * getResources().getDisplayMetrics().density);
        int margin = (int) (12 * getResources().getDisplayMetrics().density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(0, 0, margin, 0);
        swatch.setLayoutParams(params);

        GradientDrawable drawable = (GradientDrawable) ContextCompat
                .getDrawable(requireContext(), R.drawable.bg_color_swatch).mutate();
        drawable.setColor(color);
        swatch.setBackground(drawable);

        swatch.setOnClickListener(v -> {
            updateColorFromPreset(color);
            selectSwatch(swatch);
        });

        return swatch;
    }

    private void selectSwatch(View selectedView) {
        if (currentMode == 0) {
            selectedSwatchText = selectedView;
        } else {
            selectedSwatchBg = selectedView;
        }

        for (View view : swatchViews) {
            GradientDrawable drawable = (GradientDrawable) view.getBackground();
            if (view == selectedView) {
                drawable.setStroke((int) (3 * getResources().getDisplayMetrics().density), Color.WHITE);
            } else {
                drawable.setStroke(0, Color.TRANSPARENT);
            }
        }
    }

    private void setupSliders() {
        SeekBar.OnSeekBarChangeListener sliderListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentHsv[0] = seekbarHue.getProgress();
                    currentHsv[1] = seekbarSaturation.getProgress() / 100f;
                    currentHsv[2] = seekbarBrightness.getProgress() / 100f;
                    
                    if (currentMode == 1) { // Background mode
                        currentAlpha = (int) (seekbarOpacity.getProgress() * 2.55f);
                    } else {
                        currentAlpha = 255;
                    }
                    
                    currentCustomColor = Color.HSVToColor(currentAlpha, currentHsv);
                    updateCustomSwatch();
                    selectSwatch(swatchCustom);
                    updateSeekBarGradients();
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        seekbarHue.setOnSeekBarChangeListener(sliderListener);
        seekbarSaturation.setOnSeekBarChangeListener(sliderListener);
        seekbarBrightness.setOnSeekBarChangeListener(sliderListener);
        seekbarOpacity.setOnSeekBarChangeListener(sliderListener);
    }

    private void updateColorFromPreset(int color) {
        currentCustomColor = color;
        Color.colorToHSV(color, currentHsv);
        if (currentMode == 1) {
            currentAlpha = Color.alpha(color);
            seekbarOpacity.setProgress((int) (currentAlpha / 2.55f));
        } else {
            currentAlpha = 255;
        }
        updateSliders();
        updateCustomSwatch();
        updateSeekBarGradients();
    }

    private void updateUIFromColor(int color) {
        updateSliders();
        updateCustomSwatch();

        int[] presets = (currentMode == 0) ? TEXT_PRESETS : BG_PRESETS;

        boolean found = false;
        for (int i = 0; i < presets.length; i++) {
            if (presets[i] == color) {
                selectSwatch(swatchViews.get(i + 1)); 
                found = true;
                break;
            }
        }
        if (!found) {
            selectSwatch(swatchCustom);
        }
        updateSeekBarGradients();
    }

    private void updateSliders() {
        seekbarHue.setProgress((int) currentHsv[0]);
        seekbarSaturation.setProgress((int) (currentHsv[1] * 100));
        seekbarBrightness.setProgress((int) (currentHsv[2] * 100));
        if (currentMode == 1) {
            seekbarOpacity.setProgress((int) (currentAlpha / 2.55f));
        }
    }

    private void updateCustomSwatch() {
        GradientDrawable drawable = (GradientDrawable) ContextCompat
                .getDrawable(requireContext(), R.drawable.bg_color_swatch).mutate();
        
        int previewColor = (currentMode == 0) ? Color.HSVToColor(currentHsv) : currentCustomColor;
        drawable.setColor(previewColor);
        swatchCustom.setBackground(drawable);

        if (currentMode == 0) {
            if (changeListener != null) {
                changeListener.onColorChanged(previewColor);
            }
        } else {
            if (bgChangeListener != null) {
                bgChangeListener.onBackgroundChanged(currentCustomColor);
            }
        }
    }

    private void updateSeekBarGradients() {
        // Hue Gradient
        int[] hueColors = new int[7];
        for (int i = 0; i < 7; i++) {
            hueColors[i] = Color.HSVToColor(new float[] { i * 60f, 1f, 1f });
        }
        applyGradientToSeekBar(seekbarHue, hueColors);

        // Saturation Gradient
        int satStart = Color.HSVToColor(new float[] { currentHsv[0], 0f, currentHsv[2] });
        int satEnd = Color.HSVToColor(new float[] { currentHsv[0], 1f, currentHsv[2] });
        applyGradientToSeekBar(seekbarSaturation, new int[] { satStart, satEnd });

        // Brightness Gradient
        int brightStart = Color.HSVToColor(new float[] { currentHsv[0], currentHsv[1], 0f });
        int brightEnd = Color.HSVToColor(new float[] { currentHsv[0], currentHsv[1], 1f });
        applyGradientToSeekBar(seekbarBrightness, new int[] { brightStart, brightEnd });
        
        // Opacity Gradient
        int alphaStart = Color.HSVToColor(0, currentHsv);
        int alphaEnd = Color.HSVToColor(255, currentHsv);
        applyGradientToSeekBar(seekbarOpacity, new int[] { alphaStart, alphaEnd });
    }

    private void applyGradientToSeekBar(SeekBar seekBar, int[] colors) {
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        gradient.setCornerRadius(100f);
        seekBar.setProgressDrawable(gradient);
    }
}
