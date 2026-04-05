package com.grouprace.feature.tracking.ui;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Record;
import com.grouprace.core.model.ActivityStats;
import com.grouprace.core.model.RoutePoint;
import com.grouprace.feature.tracking.domain.GetActivityWithPointsUseCase;
import com.grouprace.feature.tracking.domain.SaveTitleUseCase;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ActivityDetailViewModel extends ViewModel {

    private final GetActivityWithPointsUseCase getActivityWithPointsUseCase;
    private final SaveTitleUseCase saveTitleUseCase;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final MutableLiveData<Record> record = new MutableLiveData<>();
    private final MutableLiveData<List<Point>> routePoints = new MutableLiveData<>();
    private final MutableLiveData<ActivityStats> stats = new MutableLiveData<>();
    private final MutableLiveData<String> formattedDistance = new MutableLiveData<>();
    private final MutableLiveData<String> formattedTime = new MutableLiveData<>();
    private final MutableLiveData<String> formattedPace = new MutableLiveData<>();
    private final MutableLiveData<Result<Void>> saveResult = new MutableLiveData<>();
 
    @Inject
    public ActivityDetailViewModel(SavedStateHandle savedStateHandle,
                                   GetActivityWithPointsUseCase getActivityWithPointsUseCase,
                                   SaveTitleUseCase saveTitleUseCase) {
        this.getActivityWithPointsUseCase = getActivityWithPointsUseCase;
        this.saveTitleUseCase = saveTitleUseCase;
 
        Long recordId = savedStateHandle.get("activityId");
        if (recordId != null) {
            loadRecord(recordId);
        }
    }
 
    // --- Getters for Fragment to observe ---
 
    public LiveData<Record> getRecord() { return record; }
    public LiveData<List<Point>> getRoutePoints() { return routePoints; }
    public LiveData<ActivityStats> getStats() { return stats; }
    public LiveData<String> getFormattedDistance() { return formattedDistance; }
    public LiveData<String> getFormattedTime() { return formattedTime; }
    public LiveData<String> getFormattedPace() { return formattedPace; }
    public LiveData<Result<Void>> getSaveResult() { return saveResult; }

    // --- Load record from DB ---

    private void loadRecord(long recordId) {
        executor.execute(() -> {
            GetActivityWithPointsUseCase.Result result = getActivityWithPointsUseCase.execute(recordId);

            if (result.record != null) {
                record.postValue(result.record);
                // Use record's summary data as the primary source (includes pauses)
                formattedDistance.postValue(String.format(Locale.US, "%.2f", result.record.getDistance()));
                long totalSeconds = result.record.getDuration();
                formattedTime.postValue(String.format(Locale.US, "%02d:%02d", totalSeconds / 60, totalSeconds % 60));
                formattedPace.postValue(String.format(Locale.US, "%.1f", result.record.getSpeed()));
            }

            if (result.points != null && !result.points.isEmpty()) {
                List<Point> mapPoints = new ArrayList<>();
                for (RoutePoint rp : result.points) {
                    mapPoints.add(Point.fromLngLat(rp.longitude, rp.latitude));
                }
                routePoints.postValue(mapPoints);

                // Calculate stats from points for the stats object (e.g. for charts/elevation if added)
                ActivityStats s = ActivityStats.fromPoints(result.points);
                stats.postValue(s);

                // If record data was missing, we use the fallback from points
                if (result.record == null || (result.record.getDistance() == 0 && s.distanceKm > 0)) {
                    formattedDistance.postValue(String.format(Locale.US, "%.2f", s.distanceKm));
                    long totalSeconds = s.elapsedTimeMs / 1000;
                    formattedTime.postValue(String.format(Locale.US, "%02d:%02d", totalSeconds / 60, totalSeconds % 60));
                    formattedPace.postValue(String.format(Locale.US, "%.1f", s.speedKmH));
                }
            }
        });
    }

    // --- Save title ---
 
    public void saveTitle(String title) {
        Record current = record.getValue();
        if (current != null) {
            saveResult.setValue(new Result.Loading<>());
            mainHandler.post(() -> {
                LiveData<Result<Void>> resultLiveData = saveTitleUseCase.execute(current.getRecordId(), title);
                resultLiveData.observeForever(new Observer<Result<Void>>() {
                    @Override
                    public void onChanged(Result<Void> result) {
                        saveResult.postValue(result);
                        if (result instanceof Result.Success) {
                            Record updated = new Record(
                                    current.getRecordId(), current.getActivityType(), title,
                                    current.getStartTime(), current.getEndTime(), current.getOwnerId(),
                                    current.getDuration(), current.getDistance(), current.getCalories(),
                                    current.getHeartRate(), current.getSpeed(), current.getImageUrl()
                            );
                            record.setValue(updated);
                            resultLiveData.removeObserver(this);
                        } else if (result instanceof Result.Error) {
                            Log.e("ActivityDetailVM", "Failed to update title: " + ((Result.Error<Void>) result).message);
                            resultLiveData.removeObserver(this);
                        }
                    }
                });
            });
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
