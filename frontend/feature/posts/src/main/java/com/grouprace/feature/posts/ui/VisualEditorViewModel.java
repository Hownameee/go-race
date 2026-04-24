package com.grouprace.feature.posts.ui;

import android.graphics.Color;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class VisualEditorViewModel extends ViewModel {

    private final MutableLiveData<Integer> textColor = new MutableLiveData<>(Color.WHITE);
    private final MutableLiveData<Integer> bgColor = new MutableLiveData<>(0x80000000); // 50% Black
    private final MutableLiveData<Float> scaleFactor = new MutableLiveData<>(1.0f);

    private float savedX, savedY, savedScale;

    @Inject
    public VisualEditorViewModel() {
    }

    public LiveData<Integer> getTextColor() {
        return textColor;
    }

    public void setTextColor(int color) {
        if (textColor.getValue() == null || textColor.getValue() != color) {
            textColor.setValue(color);
        }
    }

    public LiveData<Integer> getBgColor() {
        return bgColor;
    }

    public void setBgColor(int color) {
        if (bgColor.getValue() == null || bgColor.getValue() != color) {
            bgColor.setValue(color);
        }
    }

    public LiveData<Float> getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scale) {
        if (scaleFactor.getValue() == null || scaleFactor.getValue() != scale) {
            scaleFactor.setValue(scale);
        }
    }

    public void savePreviewPosition(float x, float y, float scale) {
        this.savedX = x;
        this.savedY = y;
        this.savedScale = scale;
    }

    public float getSavedX() {
        return savedX;
    }

    public float getSavedY() {
        return savedY;
    }

    public float getSavedScale() {
        return savedScale;
    }

    public void reset() {
        textColor.setValue(Color.WHITE);
        bgColor.setValue(0x80000000);
        scaleFactor.setValue(1.0f);
    }
}
