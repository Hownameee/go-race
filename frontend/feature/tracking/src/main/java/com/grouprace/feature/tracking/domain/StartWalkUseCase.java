package com.grouprace.feature.tracking.domain;

import com.grouprace.core.data.repository.TrackingRepository;

import javax.inject.Inject;

public class StartWalkUseCase {

    private final TrackingRepository repository;

    @Inject
    public StartWalkUseCase(TrackingRepository repository) {
        this.repository = repository;
    }

    public void execute() {
        repository.startNewActivity();
    }
}
