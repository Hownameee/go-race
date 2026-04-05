package com.grouprace.feature.tracking.ui;

import android.app.Application;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.RoutePoint;
import com.grouprace.core.service.LocationTrackingService;
import com.grouprace.feature.tracking.domain.FinishActivityUseCase;
import com.grouprace.feature.tracking.domain.SavePointUseCase;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TrackingViewModel extends AndroidViewModel {

    public enum TrackingState { IDLE, TRACKING, PAUSED }

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
    private static final double MIN_DISPLACEMENT_METERS = 2.0; // Filter out jitter
    private long trackingStartTime = 0;
    private long totalPausedTime = 0;
    private long pauseStartTime = 0;
    private Location lastLocation = null;
    private Location lastSavedLocation = null;

    private long totalHeartRate = 0;
    private int hrMeasureCount = 0;
    private static final double MET_RUNNING = 8.0;
    private static final double DEFAULT_WEIGHT_KG = 70.0;
 
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
    private Observer<Location> locationObserver;

    @Inject
    public TrackingViewModel(@NonNull Application application,
                             SavePointUseCase savePointUseCase,
                             FinishActivityUseCase finishActivityUseCase) {
        super(application);
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
 
    public void resetFinishedActivityId() {
        finishedActivityId.setValue(null);
    }
 
    // --- Actions ---

    /**
     * Phase 1: Start Tracking.
     * Clears unassigned points, creates a placeholder activity (ID 0), and starts GPS service.
     */
    public void startTracking() {
        backgroundExecutor.execute(() -> {
            savePointUseCase.clearUnassignedPoints(); 
            
            currentActivityId = 0;
            new Handler(Looper.getMainLooper()).post(() -> {
                trackingStartTime = System.currentTimeMillis();
                totalPausedTime = 0;
                totalDistanceMeters = 0;
                lastLocation = null;
                lastSavedLocation = null;
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
        lastLocation = null; // Reset baseline to prevent jumps on resume
        lastSavedLocation = null;
        trackingState.setValue(TrackingState.PAUSED);
    }

    public void resumeTracking() {
        totalPausedTime += System.currentTimeMillis() - pauseStartTime;
        attachLocationObserver();
        startTimer();
        trackingState.setValue(TrackingState.TRACKING);
    }

    /**
     * Phase 4: Finish Tracking.
     * Stops GPS, calculates final stats (subtracting pause), and syncs to backend.
     */
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
        float distKm = (float) (totalDistanceMeters / 1000.0);
        float speedKmH = (distKm > 0 && elapsed > 0) ? (float) (distKm / (elapsed / 3600000.0)) : 0f;
 
        long startTime = trackingStartTime;
        float hrAvg = hrMeasureCount > 0 ? (float) totalHeartRate / hrMeasureCount : 0f;
        float calories = (float) (MET_RUNNING * DEFAULT_WEIGHT_KG * (elapsed / 3600000.0));

        timerHandler.post(() -> {
            LiveData<Result<Long>> resultLiveData = finishActivityUseCase.execute(startTime, endTime, distKm, elapsed, speedKmH, hrAvg, calories);
            resultLiveData.observeForever(new Observer<Result<Long>>() {
                @Override
                public void onChanged(Result<Long> result) {
                    if (result instanceof Result.Success) {
                        finishedActivityId.setValue(((Result.Success<Long>) result).data);
                        trackingState.setValue(TrackingState.IDLE);
                        resultLiveData.removeObserver(this);
                    } else if (result instanceof Result.Error) {
                        Log.e("TrackingViewModel", "Failed to finish record: " + ((Result.Error<Long>) result).message);
                        trackingState.setValue(TrackingState.IDLE);
                        resultLiveData.removeObserver(this);
                    }
                }
            });
        });
    }

    // --- Location observer ---

    private void attachLocationObserver() {
        locationObserver = location -> {
            if (location == null) return;

            // 1. Filter out stale locations from before the 'Start' button was clicked
            if (location.getTime() < trackingStartTime) {
                return;
            }

            // 2. Initial point or significant displacement check
            if (lastSavedLocation == null) {
                // First valid point of the session
                lastSavedLocation = location;
                saveAndRecordPoint(location);
            } else {
                float displacement = lastSavedLocation.distanceTo(location);
                if (displacement >= MIN_DISPLACEMENT_METERS) {
                    totalDistanceMeters += displacement;
                    distanceKm.setValue(totalDistanceMeters / 1000.0);
                    lastSavedLocation = location;
                    saveAndRecordPoint(location);
                    updatePace();
                    calculateSimulatedHeartRate();
                }
            }
        };
        LocationTrackingService.getLocationLiveData().observeForever(locationObserver);
    }

    private void saveAndRecordPoint(Location location) {
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
        if (dist > 0.001 && elapsed != null && elapsed > 0) {
            double speedKmH = dist / (elapsed / 3600000.0);
            pace.setValue(String.format(Locale.US, "%.1f", speedKmH));
        } else {
            pace.setValue("0.0");
        }
    }

    private void calculateSimulatedHeartRate() {
        Double currentDist = distanceKm.getValue();
        Long elapsed = elapsedTimeMs.getValue();
        if (currentDist != null && elapsed != null && elapsed > 0) {
            double speedKmH = currentDist / (elapsed / 3600000.0);
            // Simulated HR formula: 70 + speedKmH * 5, capped at 190
            long currentHR = Math.min(190, Math.round(70 + speedKmH * 5));
            totalHeartRate += currentHR;
            hrMeasureCount++;
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
