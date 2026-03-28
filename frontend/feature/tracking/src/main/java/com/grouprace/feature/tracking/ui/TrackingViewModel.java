package com.grouprace.feature.tracking.ui;

import android.app.Application;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.core.service.LocationTrackingService;
import com.grouprace.feature.tracking.domain.CreateActivityUseCase;
import com.grouprace.feature.tracking.domain.FinishActivityUseCase;
import com.grouprace.feature.tracking.domain.SavePointUseCase;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TrackingViewModel extends AndroidViewModel {

    public enum TrackingState { IDLE, TRACKING, PAUSED }

    private final CreateActivityUseCase createActivityUseCase;
    private final SavePointUseCase savePointUseCase;
    private final FinishActivityUseCase finishActivityUseCase;

    private final MutableLiveData<TrackingState> trackingState = new MutableLiveData<>(TrackingState.IDLE);
    private final MutableLiveData<List<Point>> polylinePoints = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Long> elapsedTimeMs = new MutableLiveData<>(0L);
    private final MutableLiveData<Double> distanceKm = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> pace = new MutableLiveData<>("--:--");
    private final MutableLiveData<Long> finishedActivityId = new MutableLiveData<>();

    private final List<Point> routePointsCache = new ArrayList<>();
    private long currentActivityId = -1;
    private double totalDistanceMeters = 0;
    private long trackingStartTime = 0;
    private long totalPausedTime = 0;
    private long pauseStartTime = 0;
    private Location lastLocation = null;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
    private Observer<Location> locationObserver;

    @Inject
    public TrackingViewModel(@NonNull Application application,
                             CreateActivityUseCase createActivityUseCase,
                             SavePointUseCase savePointUseCase,
                             FinishActivityUseCase finishActivityUseCase) {
        super(application);
        this.createActivityUseCase = createActivityUseCase;
        this.savePointUseCase = savePointUseCase;
        this.finishActivityUseCase = finishActivityUseCase;
    }

    // --- Getters for Fragment to observe ---

    public LiveData<TrackingState> getTrackingState() { return trackingState; }
    public LiveData<List<Point>> getPolylinePoints() { return polylinePoints; }
    public LiveData<Long> getElapsedTimeMs() { return elapsedTimeMs; }
    public LiveData<Double> getDistanceKm() { return distanceKm; }
    public LiveData<String> getPace() { return pace; }
    public LiveData<Long> getFinishedActivityId() { return finishedActivityId; }

    // --- Actions ---

    public void startTracking() {
        backgroundExecutor.execute(() -> {
            currentActivityId = createActivityUseCase.execute(System.currentTimeMillis());
            new Handler(Looper.getMainLooper()).post(() -> {
                trackingStartTime = System.currentTimeMillis();
                totalPausedTime = 0;
                totalDistanceMeters = 0;
                lastLocation = null;
                routePointsCache.clear();

                getApplication().startForegroundService(
                        new Intent(getApplication(), LocationTrackingService.class)
                );
                attachLocationObserver();
                startTimer();
                trackingState.setValue(TrackingState.TRACKING);
            });
        });
    }

    public void pauseTracking() {
        detachLocationObserver();
        stopTimer();
        pauseStartTime = System.currentTimeMillis();
        trackingState.setValue(TrackingState.PAUSED);
    }

    public void resumeTracking() {
        totalPausedTime += System.currentTimeMillis() - pauseStartTime;
        attachLocationObserver();
        startTimer();
        trackingState.setValue(TrackingState.TRACKING);
    }

    public void finishTracking() {
        detachLocationObserver();
        stopTimer();

        if (trackingState.getValue() == TrackingState.PAUSED) {
            totalPausedTime += System.currentTimeMillis() - pauseStartTime;
        }

        getApplication().stopService(
                new Intent(getApplication(), LocationTrackingService.class)
        );

        long endTime = System.currentTimeMillis();
        long elapsed = endTime - trackingStartTime - totalPausedTime;
        double distKm = totalDistanceMeters / 1000.0;
        double paceVal = distKm > 0 ? (elapsed / 60000.0) / distKm : 0;

        long activityId = currentActivityId;
        backgroundExecutor.execute(() -> {
            finishActivityUseCase.execute(activityId, endTime, distKm, elapsed, paceVal);
            new Handler(Looper.getMainLooper()).post(() -> {
                finishedActivityId.setValue(activityId);
                trackingState.setValue(TrackingState.IDLE);
            });
        });
    }

    // --- Location observer ---

    private void attachLocationObserver() {
        locationObserver = location -> {
            if (location == null) return;

            // Update distance
            if (lastLocation != null) {
                totalDistanceMeters += lastLocation.distanceTo(location);
                distanceKm.setValue(totalDistanceMeters / 1000.0);
                updatePace();
            }
            lastLocation = location;

            // Add to polyline
            Point point = Point.fromLngLat(location.getLongitude(), location.getLatitude());
            routePointsCache.add(point);
            polylinePoints.setValue(new ArrayList<>(routePointsCache));

            // Save to DB via use case
            savePointUseCase.execute(new RoutePoint(
                    currentActivityId,
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getAltitude(),
                    location.getTime(),
                    location.getAccuracy()
            ));
        };
        LocationTrackingService.getLocationLiveData().observeForever(locationObserver);
    }

    private void detachLocationObserver() {
        if (locationObserver != null) {
            LocationTrackingService.getLocationLiveData().removeObserver(locationObserver);
            locationObserver = null;
        }
    }

    // --- Timer ---

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsed = System.currentTimeMillis() - trackingStartTime - totalPausedTime;
            elapsedTimeMs.setValue(elapsed);
            updatePace();
            timerHandler.postDelayed(this, 1000);
        }
    };

    private void startTimer() {
        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void updatePace() {
        double dist = totalDistanceMeters / 1000.0;
        Long elapsed = elapsedTimeMs.getValue();
        if (dist > 0.01 && elapsed != null && elapsed > 0) {
            double paceMinPerKm = (elapsed / 60000.0) / dist;
            int minutes = (int) paceMinPerKm;
            int seconds = (int) ((paceMinPerKm - minutes) * 60);
            pace.setValue(String.format("%d:%02d", minutes, seconds));
        } else {
            pace.setValue("--:--");
        }
    }

    // --- Cleanup ---

    @Override
    protected void onCleared() {
        super.onCleared();
        detachLocationObserver();
        stopTimer();
        backgroundExecutor.shutdown();
    }
}
