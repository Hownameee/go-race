package com.grouprace.core.sync.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.model.PostEntity;
import com.grouprace.core.network.model.post.CreatePostRequest;
import com.grouprace.core.network.source.PostNetworkDataSource;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class SyncPostWorker extends Worker {

    private static final String TAG = "SyncPostWorker";
    private final PostDao postDao;
    private final PostNetworkDataSource postNetworkDataSource;

    @AssistedInject
    public SyncPostWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            PostDao postDao,
            com.grouprace.core.network.source.PostNetworkDataSource postNetworkDataSource
    ) {
        super(context, workerParams);
        this.postDao = postDao;
        this.postNetworkDataSource = postNetworkDataSource;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Starting sync posts work");
        List<PostEntity> pendingPosts = postDao.getPendingPosts();
        
        if (pendingPosts.isEmpty()) {
            return Result.success();
        }

        boolean allSuccessful = true;

        for (PostEntity post : pendingPosts) {
            // Check if recordId is still negative (pending record sync)
            if (post.recordId != null && post.recordId < 0) {
                Log.d(TAG, "Post " + post.postId + " depends on negative recordId " + post.recordId + ". Retrying later.");
                allSuccessful = false;
                continue;
            }

            CreatePostRequest request = new CreatePostRequest(
                    post.recordId,
                    post.title,
                    post.description,
                    post.viewMode,
                    post.clubId
            );

            com.grouprace.core.common.result.Result<Boolean> result = postNetworkDataSource.createPostSync(
                    request,
                    post.photoUrls
            );
            
            if (result instanceof com.grouprace.core.common.result.Result.Success) {
                Log.d(TAG, "Successfully synced post: " + post.title);
                post.pendingSync = false;
                postDao.deleteById(post.postId);
            } else if (result instanceof com.grouprace.core.common.result.Result.Error) {
                com.grouprace.core.common.result.Result.Error<Boolean> error = (com.grouprace.core.common.result.Result.Error<Boolean>) result;
                Log.e(TAG, "Failed to sync post " + post.postId + ": " + error.message);
                allSuccessful = false;
            }
        }

        return allSuccessful ? Result.success() : Result.retry();
    }
}
