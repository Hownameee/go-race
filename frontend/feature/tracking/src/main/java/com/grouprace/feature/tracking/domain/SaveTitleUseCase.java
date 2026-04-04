package com.grouprace.feature.tracking.domain;
 
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
 
    public LiveData<Result<Void>> execute(long recordId, String title) {
        Record record = repository.getRecordById(recordId);
        if (record != null) {
            Record updated = new Record(
                    record.getRecordId(), record.getActivityType(), title,
                    record.getStartTime(), record.getEndTime(), record.getOwnerId(),
                    record.getDuration(), record.getDistance(), record.getCalories(),
                    record.getHeartRate(), record.getSpeed(), record.getImageUrl()
            );
            return repository.updateRecord(updated);
        }
        return new MutableLiveData<>(new Result.Error<>(new Exception("Record not found"), "Record not found"));
    }
}
