package com.grouprace.feature.tracking.domain;

import com.grouprace.feature.tracking.data.TrackingRepository;

public class StartWalkUseCase {

    private final TrackingRepository repository;

    public StartWalkUseCase(TrackingRepository repository) {
        this.repository = repository;
    }

    public void execute() {
        repository.startNewActivity();
    }
}
