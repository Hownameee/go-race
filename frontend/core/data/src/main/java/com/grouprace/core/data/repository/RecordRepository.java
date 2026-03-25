package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Record;

import java.util.List;

public interface RecordRepository {
    LiveData<Result<List<Record>>> getRecords(int offset);
}
