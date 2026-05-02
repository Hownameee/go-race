package com.grouprace.feature.tracking.domain;
 
import androidx.lifecycle.LiveData;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Record;
import com.grouprace.core.data.repository.TrackingRepository;
 
import javax.inject.Inject;
 
public class SaveTitleUseCase {
 
    private final TrackingRepository repository;
 
    @Inject
    public SaveTitleUseCase(TrackingRepository repository) {
        this.repository = repository;
    }
 
    public LiveData<Result<Void>> execute(Record record, String title) {
        Record updated = new Record(
                record.getRecordId(), record.getActivityType(), title,
                record.getStartTime(), record.getEndTime(), record.getOwnerId(),
                record.getDuration(), record.getDistance(), record.getCalories(),
                record.getHeartRate(), record.getSpeed(), record.getImageUrl()
        );
        return repository.updateRecord(updated);
    }
}
