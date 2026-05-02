package com.grouprace.feature.tracking.domain;

import androidx.lifecycle.LiveData;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Record;
import com.grouprace.core.data.repository.TrackingRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

public class FinishActivityUseCase {

    private final TrackingRepository repository;

    @Inject
    public FinishActivityUseCase(TrackingRepository repository) {
        this.repository = repository;
    }

    /**
     * Prepares the record model for delivery to the repository.
     * Note: Duration and distance are received directly from the UI tracking logic 
     * to keep them perfectly in sync with the user's view.
     */
    public LiveData<Result<Long>> execute(String title, long startTime, long endTime, float distanceKm,
                                         long elapsedTimeMs, float speedKmH,
                                         float heartRateAvg, float caloriesBurned,
                                         java.util.List<com.grouprace.core.model.RoutePoint> points) {
        int durationSeconds = (int) (elapsedTimeMs / 1000);
        // speedKmh calculation for safety
        float speedKmhCalculated = elapsedTimeMs > 0
                ? (float) (distanceKm / (elapsedTimeMs / 3_600_000.0))
                : 0.0f;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        Record record = new Record(
                0, "Running", title != null ? title : "New Activity",
                sdf.format(new Date(startTime)),
                sdf.format(new Date(endTime)),
                0, durationSeconds, distanceKm,
                caloriesBurned, heartRateAvg, speedKmH, null
        );
        return repository.createRecord(record, points);
    }
}
