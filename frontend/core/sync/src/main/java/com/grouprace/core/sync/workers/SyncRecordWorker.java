package com.grouprace.core.sync.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.PostDao;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class SyncRecordWorker extends Worker {

    private final RecordDao recordDao;
    private final PostDao postDao;

    @AssistedInject
    public SyncRecordWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            RecordDao recordDao,
            PostDao postDao
    ) {
        super(context, workerParams);
        this.recordDao = recordDao;
        this.postDao = postDao;
    }

    @NonNull
    @Override
    public Result doWork() {
        // Implementation commented out as requested
        /*
        List<RecordEntity> pendingRecords = recordDao.getPendingRecords();
        for (RecordEntity record : pendingRecords) {
            // 1. Upload to backend
            // 2. Get realId
            // 3. Update RecordEntity (delete old, insert new)
            // 4. Update dependent Posts
            // postDao.updatePendingPostRecordIds(record.recordId, realId);
        }
        */
        return Result.success();
    }
}
