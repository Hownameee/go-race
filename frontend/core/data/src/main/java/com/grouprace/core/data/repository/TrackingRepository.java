package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Record;
import com.grouprace.core.model.RoutePoint;

import java.util.List;

public interface TrackingRepository {

    void savePoint(RoutePoint point);

    LiveData<Result<Long>> createRecord(Record record);
 
    LiveData<Result<Void>> updateRecord(Record record);
 
    void updateRecordLocal(Record record);

    Record getRecordById(long id);

    List<RoutePoint> getPointsForRecord(long recordId);
 
    void clearUnassignedPoints();
}
