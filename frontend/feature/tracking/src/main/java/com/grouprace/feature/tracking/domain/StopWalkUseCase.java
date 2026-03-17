package com.grouprace.feature.tracking.domain;

import com.grouprace.feature.tracking.data.TrackingRepository;

public class StopWalkUseCase {

    private final TrackingRepository repository;

    public StopWalkUseCase(TrackingRepository repository) {
        this.repository = repository;
    }

    public void execute() {
        repository.stopActivity();
    }
}
