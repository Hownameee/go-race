package com.grouprace.feature.tracking.ui;

import android.app.Application;
import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.service.LocationTrackingService;
import com.grouprace.core.data.repository.TrackingRepository;
import com.grouprace.feature.tracking.domain.StartWalkUseCase;
import com.grouprace.feature.tracking.domain.StopWalkUseCase;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TrackingViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isTracking = new MutableLiveData<>(false);
    private final TrackingRepository repository;
    private final StartWalkUseCase startWalkUseCase;
    private final StopWalkUseCase stopWalkUseCase;

    @Inject
    public TrackingViewModel(@NonNull Application application, TrackingRepository repository, StartWalkUseCase startWalkUseCase, StopWalkUseCase stopWalkUseCase) {
        super(application);
        this.repository = repository;
        this.startWalkUseCase = startWalkUseCase;
        this.stopWalkUseCase = stopWalkUseCase;
    }

    public MutableLiveData<Boolean> getIsTracking() {
        return isTracking;
    }

    public LiveData<Location> getCurrentLocation() {
        return repository.getCurrentLocation();
    }

    public void startTracking() {
        startWalkUseCase.execute();
        Intent intent = new Intent(getApplication(), LocationTrackingService.class);
        getApplication().startForegroundService(intent);
        isTracking.setValue(true);
    }

    public void stopTracking() {
        stopWalkUseCase.execute();
        Intent intent = new Intent(getApplication(), LocationTrackingService.class);
        getApplication().stopService(intent);
        isTracking.setValue(false);
    }
}
