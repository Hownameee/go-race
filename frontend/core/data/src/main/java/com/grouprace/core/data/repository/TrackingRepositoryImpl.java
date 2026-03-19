package com.grouprace.core.data.repository;

import android.location.Location;

import androidx.lifecycle.LiveData;

import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.core.service.LocationTrackingService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;

public class TrackingRepositoryImpl implements TrackingRepository {

    private final RoutePointDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public TrackingRepositoryImpl(RoutePointDao dao) {
        this.dao = dao;
    }

    @Override
    public void startNewActivity() {
        // Activity start logic (e.g. record start time) can be added here
    }

    @Override
    public void savePoint(RoutePoint point) {
        executor.execute(() -> dao.insert(point));
    }

    @Override
    public LiveData<Location> getCurrentLocation() {
        return LocationTrackingService.locationLiveData;
    }

    @Override
    public void stopActivity() {
        // Activity stop logic (e.g. record end time) can be added here
    }
}
