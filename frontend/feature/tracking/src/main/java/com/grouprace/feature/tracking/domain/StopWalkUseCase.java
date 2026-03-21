package com.grouprace.feature.tracking.domain;

import com.grouprace.core.data.repository.TrackingRepository;

import javax.inject.Inject;

public class StopWalkUseCase {

    private final TrackingRepository repository;

    @Inject
    public StopWalkUseCase(TrackingRepository repository) {
        this.repository = repository;
    }

    public void execute() {
        repository.stopActivity();
    }
}
