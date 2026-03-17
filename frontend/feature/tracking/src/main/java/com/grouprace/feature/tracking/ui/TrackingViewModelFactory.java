package com.grouprace.feature.tracking.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.feature.tracking.data.TrackingRepository;

public class TrackingViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final TrackingRepository repository;

    public TrackingViewModelFactory(Application application, TrackingRepository repository) {
        this.application = application;
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TrackingViewModel.class)) {
            //noinspection unchecked
            return (T) new TrackingViewModel(application, repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
