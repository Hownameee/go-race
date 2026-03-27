package com.grouprace.feature.tracking.ui;

import android.app.Application;
import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.core.data.repository.TrackingRepository;
import com.grouprace.core.service.LocationTrackingService;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TrackingViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isTracking = new MutableLiveData<>(false);
    private final TrackingRepository repository;
    private final Observer<Location> savePointObserver;

    @Inject
    public TrackingViewModel(@NonNull Application application, TrackingRepository repository) {
        super(application);
        this.repository = repository;
        savePointObserver = location -> {
            if (location != null) {
                repository.savePoint(new RoutePoint(
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude(),
                        location.getTime(),
                        location.getAccuracy()
                ));
            }
        };
    }

    public LiveData<Boolean> getIsTracking() {
        return isTracking;
    }

    public LiveData<Location> getCurrentLocation() {
        return LocationTrackingService.getLocationLiveData();
    }

    public void startTracking() {
        getApplication().startForegroundService(
                new Intent(getApplication(), LocationTrackingService.class)
        );
        LocationTrackingService.getLocationLiveData().observeForever(savePointObserver);
        isTracking.setValue(true);
    }

    public void stopTracking() {
        LocationTrackingService.getLocationLiveData().removeObserver(savePointObserver);
        getApplication().stopService(
                new Intent(getApplication(), LocationTrackingService.class)
        );
        isTracking.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        LocationTrackingService.getLocationLiveData().removeObserver(savePointObserver);
    }
}
