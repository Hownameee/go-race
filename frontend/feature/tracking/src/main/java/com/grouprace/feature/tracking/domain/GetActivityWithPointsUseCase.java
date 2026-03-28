package com.grouprace.feature.tracking.domain;

import com.grouprace.core.data.model.Activity;
import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.core.data.repository.TrackingRepository;

import java.util.List;

import javax.inject.Inject;

public class GetActivityWithPointsUseCase {

    private final TrackingRepository repository;

    @Inject
    public GetActivityWithPointsUseCase(TrackingRepository repository) {
        this.repository = repository;
    }

    public Result execute(long activityId) {
        Activity activity = repository.getActivityById(activityId);
        List<RoutePoint> points = repository.getPointsForActivity(activityId);
        return new Result(activity, points);
    }

    public static class Result {
        public final Activity activity;
        public final List<RoutePoint> points;

        public Result(Activity activity, List<RoutePoint> points) {
            this.activity = activity;
            this.points = points;
        }
    }
}
