package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Record;

import java.util.List;

public interface RecordRepository {
    void getNetworkRecord(int recordId);
    LiveData<List<Record>> getLocalRecords(int limit);
    LiveData<Result<Boolean>> getNetworkRecords(int offset, int limit);
}
