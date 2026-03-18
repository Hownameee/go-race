package com.grouprace.feature.tracking.domain;

import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.core.data.repository.TrackingRepository;

import javax.inject.Inject;

public class SavePointUseCase {

    private final TrackingRepository repository;

    @Inject
    public SavePointUseCase(TrackingRepository repository) {
        this.repository = repository;
    }

    public void execute(RoutePoint point) {
        repository.savePoint(point);
    }
}
