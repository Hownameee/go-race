package com.grouprace.core.sync.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.grouprace.core.data.dao.ProfileDao;
import com.grouprace.core.data.model.MyProfileInfoEntity;
import com.grouprace.core.data.model.ProfileOverviewEntity;
import com.grouprace.core.network.model.user.MyProfileInfoPayload;
import com.grouprace.core.network.model.user.ProfileOverviewResponse;
import com.grouprace.core.network.source.UserNetworkDataSource;
import com.grouprace.core.network.utils.SessionManager;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class SyncUserProfileWorker extends Worker {

    private static final String TAG = "SyncUserProfileWorker";

    private final UserNetworkDataSource userNetworkDataSource;
    private final ProfileDao profileDao;
    private final SessionManager sessionManager;

    @AssistedInject
    public SyncUserProfileWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            UserNetworkDataSource userNetworkDataSource,
            ProfileDao profileDao,
            SessionManager sessionManager
    ) {
        super(context, workerParams);
        this.userNetworkDataSource = userNetworkDataSource;
        this.profileDao = profileDao;
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    public Result doWork() {
        int currentUserId = sessionManager.getUserId();
        if (currentUserId <= 0) {
            Log.d(TAG, "Skipping profile sync because current user id is unavailable.");
            return Result.success();
        }

        try {
            ProfileOverviewResponse overview = userNetworkDataSource.getMyOverviewSync();
            MyProfileInfoPayload info = userNetworkDataSource.getMyInfoSync();

            profileDao.upsertOverview(new ProfileOverviewEntity(
                    overview.getUserId(),
                    true,
                    overview.getFullname(),
                    overview.getAvatarUrl(),
                    overview.getBio(),
                    overview.getCity(),
                    overview.getCountry(),
                    overview.getTotalFollowings(),
                    overview.getTotalFollowers(),
                    overview.isFollowing()
            ));
            profileDao.clearSelfOverviewExcept(overview.getUserId());

            profileDao.upsertMyInfo(new MyProfileInfoEntity(
                    currentUserId,
                    info.getUsername(),
                    info.getFullname(),
                    info.getEmail(),
                    info.getBirthdate(),
                    info.getAvatarUrl(),
                    info.getBio(),
                    info.getProvinceCity(),
                    info.getCountry(),
                    info.getHeightCm(),
                    info.getWeightKg()
            ));
            profileDao.clearMyInfoExcept(currentUserId);

            return Result.success();
        } catch (Exception exception) {
            Log.e(TAG, "Failed to sync user profile", exception);
            return Result.retry();
        }
    }
}
