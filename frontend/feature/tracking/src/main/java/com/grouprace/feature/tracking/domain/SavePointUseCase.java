package com.grouprace.feature.tracking.domain;

import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.feature.tracking.data.TrackingRepository;

public class SavePointUseCase {

    private final TrackingRepository repository;

    public SavePointUseCase(TrackingRepository repository) {
        this.repository = repository;
    }

    public void execute(RoutePoint point) {
        repository.savePoint(point);
    }
}
