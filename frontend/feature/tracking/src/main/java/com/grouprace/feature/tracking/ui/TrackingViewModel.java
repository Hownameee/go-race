package com.grouprace.feature.tracking.ui;

import android.app.Application;
import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.grouprace.core.data.AppDatabase;
import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.core.service.LocationTrackingService;
import com.grouprace.feature.tracking.data.TrackingRepository;
import com.grouprace.feature.tracking.data.TrackingRepositoryImpl;

public class TrackingViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isTracking = new MutableLiveData<>(false);
    private final TrackingRepository repository;
    private final Observer<Location> savePointObserver;

    public TrackingViewModel(@NonNull Application application) {
        super(application);
        repository = new TrackingRepositoryImpl(
                AppDatabase.getInstance(application).routePointDao()
        );
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
