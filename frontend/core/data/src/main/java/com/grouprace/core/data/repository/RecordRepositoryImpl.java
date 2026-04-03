package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.model.RecordEntity;
import com.grouprace.core.model.Record;
import com.grouprace.core.network.model.NetworkRecord;
import com.grouprace.core.network.source.RecordNetworkDataSource;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class RecordRepositoryImpl implements RecordRepository {

    private final RecordNetworkDataSource recordNetworkDataSource;
    private final RecordDao recordDao;

    @Inject
    public RecordRepositoryImpl(RecordNetworkDataSource recordNetworkDataSource, RecordDao recordDao) {
        this.recordNetworkDataSource = recordNetworkDataSource;
        this.recordDao = recordDao;
    }

    @Override
    public LiveData<List<Record>> getRecords(int limit) {
        return Transformations.map(recordDao.getRecords(limit), entities -> entities.stream().map(RecordEntity::asExternalModel).collect(Collectors.toList()));
    }

    @Override
    public LiveData<Result<Boolean>> syncRecords(int currentId) {
        MutableLiveData<Result<Boolean>> resultData = new MutableLiveData<>();
        resultData.postValue(new Result.Loading<>());

        LiveData<Result<List<NetworkRecord>>> networkCall = recordNetworkDataSource.getAllRecords();
//        LiveData<Result<List<NetworkRecord>>> networkCall = recordNetworkDataSource.getRecords(currentId);

        networkCall.observeForever(result -> {
            if (result instanceof Result.Success) {
                List<NetworkRecord> networkRecords = ((Result.Success<List<NetworkRecord>>) result).data;

                if (networkRecords != null) {
                    List<RecordEntity> entities = networkRecords.stream().map(n -> new RecordEntity(n.getRecordId(), n.getActivityType(), n.getTitle(), n.getStartTime(), n.getEndTime(), n.getOwnerId(), n.getDuration(), n.getDistance(), n.getCalories(), n.getHeartRate(), n.getSpeed(), n.getImageUrl())).collect(Collectors.toList());

                    new Thread(() -> {
                        recordDao.insertAll(entities);
                        resultData.postValue(new Result.Success<>(true));
                    }).start();
                } else {
                    resultData.postValue(new Result.Success<>(true));
                }

            } else if (result instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) result;
                resultData.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return resultData;
    }
}
