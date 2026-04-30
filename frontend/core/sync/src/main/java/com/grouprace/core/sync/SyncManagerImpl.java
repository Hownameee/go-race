package com.grouprace.core.sync;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.grouprace.core.data.SyncManager;
import com.grouprace.core.sync.workers.SyncPostWorker;
import com.grouprace.core.sync.workers.SyncRecordWorker;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class SyncManagerImpl implements SyncManager {

    private final Context context;
    private final WorkManager workManager;

    @Inject
    public SyncManagerImpl(@ApplicationContext Context context) {
        this.context = context;
        this.workManager = WorkManager.getInstance(context);
    }

    @Override
    public void scheduleRecordSync() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SyncRecordWorker.class)
                .setConstraints(constraints)
                .build();

        workManager.enqueueUniqueWork(
                "sync_records",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
        );
    }

    @Override
    public void schedulePostSync() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SyncPostWorker.class)
                .setConstraints(constraints)
                .build();

        workManager.enqueueUniqueWork(
                "sync_posts",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
        );
    }
}
