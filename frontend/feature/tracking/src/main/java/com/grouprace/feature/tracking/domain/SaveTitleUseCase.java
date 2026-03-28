package com.grouprace.feature.tracking.domain;

import com.grouprace.core.data.model.Activity;
import com.grouprace.core.data.repository.TrackingRepository;

import javax.inject.Inject;

public class SaveTitleUseCase {

    private final TrackingRepository repository;

    @Inject
    public SaveTitleUseCase(TrackingRepository repository) {
        this.repository = repository;
    }

    public void execute(long activityId, String title) {
        Activity activity = repository.getActivityById(activityId);
        if (activity != null) {
            activity.title = title;
            repository.updateActivity(activity);
        }
    }
}
