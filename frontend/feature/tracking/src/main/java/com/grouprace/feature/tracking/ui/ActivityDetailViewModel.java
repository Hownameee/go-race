package com.grouprace.feature.tracking.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.data.model.Activity;
import com.grouprace.core.data.model.ActivityStats;
import com.grouprace.core.data.model.RoutePoint;
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

    private final MutableLiveData<Activity> activity = new MutableLiveData<>();
    private final MutableLiveData<List<Point>> routePoints = new MutableLiveData<>();
    private final MutableLiveData<ActivityStats> stats = new MutableLiveData<>();
    private final MutableLiveData<String> formattedDistance = new MutableLiveData<>();
    private final MutableLiveData<String> formattedTime = new MutableLiveData<>();
    private final MutableLiveData<String> formattedPace = new MutableLiveData<>();

    @Inject
    public ActivityDetailViewModel(SavedStateHandle savedStateHandle,
                                   GetActivityWithPointsUseCase getActivityWithPointsUseCase,
                                   SaveTitleUseCase saveTitleUseCase) {
        this.getActivityWithPointsUseCase = getActivityWithPointsUseCase;
        this.saveTitleUseCase = saveTitleUseCase;

        Long activityId = savedStateHandle.get("activityId");
        if (activityId != null) {
            loadActivity(activityId);
        }
    }

    // --- Getters for Fragment to observe ---

    public LiveData<Activity> getActivity() { return activity; }
    public LiveData<List<Point>> getRoutePoints() { return routePoints; }
    public LiveData<ActivityStats> getStats() { return stats; }
    public LiveData<String> getFormattedDistance() { return formattedDistance; }
    public LiveData<String> getFormattedTime() { return formattedTime; }
    public LiveData<String> getFormattedPace() { return formattedPace; }

    // --- Load activity from DB ---

    private void loadActivity(long activityId) {
        executor.execute(() -> {
            GetActivityWithPointsUseCase.Result result = getActivityWithPointsUseCase.execute(activityId);

            if (result.activity != null) {
                activity.postValue(result.activity);
            }

            if (result.points != null && !result.points.isEmpty()) {
                List<Point> mapPoints = new ArrayList<>();
                for (RoutePoint rp : result.points) {
                    mapPoints.add(Point.fromLngLat(rp.longitude, rp.latitude));
                }
                routePoints.postValue(mapPoints);

                ActivityStats s = ActivityStats.fromPoints(result.points);
                stats.postValue(s);

                formattedDistance.postValue(String.format(Locale.US, "%.2f", s.distanceKm));
                long totalSeconds = s.elapsedTimeMs / 1000;
                formattedTime.postValue(String.format(Locale.US, "%02d:%02d", totalSeconds / 60, totalSeconds % 60));
                int paceMin = (int) s.paceMinPerKm;
                int paceSec = (int) ((s.paceMinPerKm - paceMin) * 60);
                formattedPace.postValue(String.format(Locale.US, "%d:%02d", paceMin, paceSec));
            }
        });
    }

    // --- Save title (used by Summary screen) ---

    public void saveTitle(String title) {
        Activity current = activity.getValue();
        if (current != null) {
            executor.execute(() -> saveTitleUseCase.execute(current.id, title));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
