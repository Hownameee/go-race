package com.grouprace.feature.tracking.domain;

import com.grouprace.core.data.model.Activity;
import com.grouprace.core.data.repository.TrackingRepository;

import javax.inject.Inject;

public class FinishActivityUseCase {

    private final TrackingRepository repository;

    @Inject
    public FinishActivityUseCase(TrackingRepository repository) {
        this.repository = repository;
    }

    public void execute(long activityId, long endTime, double distanceKm,
                        long elapsedTimeMs, double paceMinPerKm) {
        Activity activity = repository.getActivityById(activityId);
        if (activity != null) {
            activity.endTime = endTime;
            activity.distanceKm = distanceKm;
            activity.elapsedTimeMs = elapsedTimeMs;
            activity.paceMinPerKm = paceMinPerKm;
            activity.completed = true;
            repository.updateActivity(activity);
        }
    }
}
