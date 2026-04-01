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
    public LiveData<Result<Long>> execute(long startTime, long endTime, double distanceKm,
                                         long elapsedTimeMs, double speedKmH) {
        int durationSeconds = (int) (elapsedTimeMs / 1000);
        // speedKmh calculation for safety
        double speedKmh = elapsedTimeMs > 0
                ? distanceKm / (elapsedTimeMs / 3_600_000.0)
                : 0.0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Record record = new Record(
                0, "Running", "New Activity",
                sdf.format(new Date(startTime)),
                sdf.format(new Date(endTime)),
                0, durationSeconds, distanceKm,
                0, 0, speedKmh, null
        );
        return repository.createRecord(record);
    }
}
