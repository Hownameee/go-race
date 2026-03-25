package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Record;
import com.grouprace.core.network.model.NetworkRecord;
import com.grouprace.core.network.source.RecordNetworkDataSource;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class RecordRepositoryImpl implements RecordRepository {

    private final RecordNetworkDataSource recordNetworkDataSource;

    @Inject
    public RecordRepositoryImpl(RecordNetworkDataSource recordNetworkDataSource) {
        this.recordNetworkDataSource = recordNetworkDataSource;
    }

    @Override
    public LiveData<Result<List<Record>>> getRecords(int offset) {
        return Transformations.map(recordNetworkDataSource.getRecords(1, offset), result -> {
            if (result instanceof Result.Success) {
                List<NetworkRecord> networkRecords = ((Result.Success<List<NetworkRecord>>) result).data;
                List<Record> records = networkRecords.stream()
                        .map(NetworkRecord::asExternalModel)
                        .collect(Collectors.toList());
                return new Result.Success<>(records);
            } else if (result instanceof Result.Error) {
                Result.Error<List<NetworkRecord>> error = (Result.Error<List<NetworkRecord>>) result;
                return new Result.Error<>(error.exception, error.message);
            } else {
                return new Result.Loading<>();
            }
        });
    }
}
