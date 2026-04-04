package com.grouprace.feature.tracking.domain;

import com.grouprace.core.model.Record;
import com.grouprace.core.model.RoutePoint;
import com.grouprace.core.data.repository.TrackingRepository;

import java.util.List;

import javax.inject.Inject;

public class GetActivityWithPointsUseCase {

    private final TrackingRepository repository;

    @Inject
    public GetActivityWithPointsUseCase(TrackingRepository repository) {
        this.repository = repository;
    }

    public Result execute(long recordId) {
        Record record = repository.getRecordById(recordId);
        List<RoutePoint> points = repository.getPointsForRecord(recordId);
        return new Result(record, points);
    }

    public static class Result {
        public final Record record;
        public final List<RoutePoint> points;

        public Result(Record record, List<RoutePoint> points) {
            this.record = record;
            this.points = points;
        }
    }
}
