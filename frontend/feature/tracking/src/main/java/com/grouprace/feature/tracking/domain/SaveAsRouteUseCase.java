package com.grouprace.feature.tracking.domain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.UserRouteRepository;
import com.grouprace.core.model.UserRoute;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SaveAsRouteUseCase {

    private final UserRouteRepository userRouteRepository;

    @Inject
    public SaveAsRouteUseCase(UserRouteRepository userRouteRepository) {
        this.userRouteRepository = userRouteRepository;
    }

    public LiveData<Result<Long>> execute(String name, List<Point> points, double distanceKm, int durationSeconds) {
        if (points == null || points.size() < 2) {
            MutableLiveData<Result<Long>> err = new MutableLiveData<>();
            err.setValue(new Result.Error<>(new Exception("Invalid points"), "Invalid points"));
            return err;
        }

        List<double[]> coords = new ArrayList<>();
        for (Point p : points) {
            coords.add(new double[]{p.longitude(), p.latitude()});
        }

        List<double[]> waypoints = List.of(coords.get(0), coords.get(coords.size() - 1));

        UserRoute route = new UserRoute(0, name, waypoints, coords,
                distanceKm, durationSeconds, "recorded", false,
                System.currentTimeMillis());

        return userRouteRepository.saveRoute(route);
    }
}
