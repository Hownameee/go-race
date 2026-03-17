package com.grouprace.feature.tracking.ui;

import android.app.Application;
import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.service.LocationTrackingService;
import com.grouprace.feature.tracking.data.TrackingRepository;
import com.grouprace.feature.tracking.domain.StartWalkUseCase;
import com.grouprace.feature.tracking.domain.StopWalkUseCase;

public class TrackingViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isTracking = new MutableLiveData<>(false);
    private final TrackingRepository repository;
    private final StartWalkUseCase startWalkUseCase;
    private final StopWalkUseCase stopWalkUseCase;

    public TrackingViewModel(@NonNull Application application, TrackingRepository repository) {
        super(application);
        this.repository = repository;
        this.startWalkUseCase = new StartWalkUseCase(repository);
        this.stopWalkUseCase = new StopWalkUseCase(repository);
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
