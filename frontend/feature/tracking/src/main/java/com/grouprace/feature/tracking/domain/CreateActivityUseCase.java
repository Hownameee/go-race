package com.grouprace.feature.tracking.domain;

import com.grouprace.core.data.model.Activity;
import com.grouprace.core.data.repository.TrackingRepository;

import javax.inject.Inject;

public class CreateActivityUseCase {

    private final TrackingRepository repository;

    @Inject
    public CreateActivityUseCase(TrackingRepository repository) {
        this.repository = repository;
    }

    public long execute(long startTime) {
        Activity activity = new Activity();
        activity.startTime = startTime;
        activity.completed = false;
        return repository.createActivity(activity);
    }
}
