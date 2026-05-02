package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.dao.ProfileDao;
import com.grouprace.core.data.model.MyProfileInfoEntity;
import com.grouprace.core.data.model.ProfileCacheEntity;
import com.grouprace.core.data.model.ProfileOverviewEntity;
import com.grouprace.core.model.Profile.FollowUser;
import com.grouprace.core.model.Profile.MyProfileInfo;
import com.grouprace.core.model.Profile.ProfileOverview;
import com.grouprace.core.network.model.user.FollowListResponse;
import com.grouprace.core.network.model.user.FollowUserResponse;
import com.grouprace.core.network.model.user.MyProfileInfoPayload;
import com.grouprace.core.network.model.user.ProfileOverviewResponse;
import com.grouprace.core.network.source.UserNetworkDataSource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class UserRepositoryImpl implements UserRepository {

    private static final int MY_PROFILE_INFO_ID = 1;

    private final UserNetworkDataSource userNetworkDataSource;
    private final com.grouprace.core.network.utils.SessionManager sessionManager;
    private final ProfileDao profileDao;
    // ===== Profile Feature Section =====
    private final Gson gson = new Gson();

    @Inject
    public UserRepositoryImpl(UserNetworkDataSource userNetworkDataSource, com.grouprace.core.network.utils.SessionManager sessionManager, ProfileDao profileDao) {
        this.userNetworkDataSource = userNetworkDataSource;
        this.sessionManager = sessionManager;
        this.profileDao = profileDao;
    }

    // ===== Profile Feature Section =====
    @Override
    public LiveData<Result<ProfileOverview>> getMyOverview() {
        return getOverviewOfflineFirst(
                profileDao.getMyOverview(),
                userNetworkDataSource.getMyOverview(),
                true
        );
    }

    @Override
    public LiveData<Result<ProfileOverview>> getUserOverview(int userId) {
        return getOverviewOfflineFirst(
                profileDao.getUserOverview(userId),
                userNetworkDataSource.getUserOverview(userId),
                false
        );
    }

    // ===== Profile Feature Section =====
    @Override
    public LiveData<Result<List<FollowUser>>> getFollowers(int userId) {
        return getFollowListOfflineFirst(
                profileFollowCacheKey(userId, true),
                userNetworkDataSource.getFollowers(userId),
                true
        );
    }

    // ===== Profile Feature Section =====
    @Override
    public LiveData<Result<List<FollowUser>>> getFollowing(int userId) {
        return getFollowListOfflineFirst(
                profileFollowCacheKey(userId, false),
                userNetworkDataSource.getFollowing(userId),
                false
        );
    }

    // ===== Profile Feature Section =====
    @Override
    public LiveData<Result<MyProfileInfo>> getMyInfo() {
        MediatorLiveData<Result<MyProfileInfo>> resultData = new MediatorLiveData<>();
        boolean[] hasLocal = { false };

        LiveData<MyProfileInfoEntity> localResult = profileDao.getMyInfo();
        resultData.addSource(localResult, entity -> {
            if (entity != null) {
                hasLocal[0] = true;
                resultData.setValue(new Result.Success<>(entity.asExternalModel()));
            }
        });

        LiveData<Result<MyProfileInfoPayload>> networkResult = userNetworkDataSource.getMyInfo();
        resultData.addSource(networkResult, result -> {
            if (result instanceof Result.Loading) {
                if (!hasLocal[0]) {
                    resultData.setValue(new Result.Loading<>());
                }
            } else if (result instanceof Result.Success) {
                MyProfileInfoPayload response = ((Result.Success<MyProfileInfoPayload>) result).data;
                MyProfileInfo info = mapToMyProfileInfo(response);
                if (info != null) {
                    cacheMyInfo(info);
                }
                resultData.setValue(new Result.Success<>(info));
            } else if (!hasLocal[0]) {
                Result.Error<MyProfileInfoPayload> error = (Result.Error<MyProfileInfoPayload>) result;
                resultData.setValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return resultData;
    }

    @Override
    public LiveData<Result<Void>> requestEmailChangeOtp() {
        return userNetworkDataSource.requestEmailChangeOtp();
    }

    @Override
    public LiveData<Result<Void>> verifyEmailChangeOtp(String otpCode) {
        return userNetworkDataSource.verifyEmailChangeOtp(otpCode);
    }

    @Override
    public LiveData<Result<Void>> requestNewEmailChangeOtp(String newEmail) {
        return userNetworkDataSource.requestNewEmailChangeOtp(newEmail);
    }

    @Override
    public LiveData<Result<Void>> confirmEmailChange(String newEmail, String otpCode) {
        return userNetworkDataSource.confirmEmailChange(newEmail, otpCode);
    }

    @Override
    public LiveData<Result<Void>> verifyCurrentPassword(String oldPassword) {
        return userNetworkDataSource.verifyCurrentPassword(oldPassword);
    }

    @Override
    public LiveData<Result<Void>> changePassword(String oldPassword, String newPassword, String confirmNewPassword) {
        return userNetworkDataSource.changePassword(oldPassword, newPassword, confirmNewPassword);
    }

    @Override
    public LiveData<Result<Void>> requestPasswordResetOtp() {
        return userNetworkDataSource.requestPasswordResetOtp();
    }

    @Override
    public LiveData<Result<Void>> resetPasswordWithOtp(String otpCode, String newPassword, String confirmNewPassword) {
        return userNetworkDataSource.resetPasswordWithOtp(otpCode, newPassword, confirmNewPassword);
    }

    @Override
    public LiveData<Result<Void>> deleteMyAccount() {
        return userNetworkDataSource.deleteMyAccount();
    }

    @Override
    public LiveData<Result<String>> uploadMyAvatar(byte[] avatarBytes, String fileName, String mimeType) {
        return userNetworkDataSource.uploadMyAvatar(avatarBytes, fileName, mimeType);
    }

    // ===== Profile Feature Section =====
    @Override
    public LiveData<Result<Void>> updateMyInfo(MyProfileInfo myProfileInfo) {
        MutableLiveData<Result<Void>> resultData = new MutableLiveData<>();
        resultData.postValue(new Result.Loading<>());

        userNetworkDataSource.updateMyInfo(mapToMyProfileInfoPayload(myProfileInfo)).observeForever(result -> {
            if (result instanceof Result.Success) {
                userNetworkDataSource.getMyInfo().observeForever(syncResult -> {
                    if (syncResult instanceof Result.Success) {
                        cacheMyInfo(mapToMyProfileInfo(((Result.Success<MyProfileInfoPayload>) syncResult).data));
                    }
                });
                resultData.postValue(new Result.Success<>(null));
            } else if (result instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) result;
                resultData.postValue(new Result.Error<>(error.exception, error.message));
            } else {
                resultData.postValue(new Result.Loading<>());
            }
        });

        return resultData;
    }

    // ===== Profile Feature Section =====
    private LiveData<Result<ProfileOverview>> getOverviewOfflineFirst(
            LiveData<ProfileOverviewEntity> localResult,
            LiveData<Result<ProfileOverviewResponse>> networkResult,
            boolean selfProfile
    ) {
        MediatorLiveData<Result<ProfileOverview>> resultData = new MediatorLiveData<>();
        boolean[] hasLocal = { false };

        resultData.addSource(localResult, entity -> {
            if (entity != null) {
                hasLocal[0] = true;
                resultData.setValue(new Result.Success<>(entity.asExternalModel()));
            }
        });

        resultData.addSource(networkResult, result -> {
            if (result instanceof Result.Loading) {
                if (!hasLocal[0]) {
                    resultData.setValue(new Result.Loading<>());
                }
            } else if (result instanceof Result.Success) {
                ProfileOverviewResponse response = ((Result.Success<ProfileOverviewResponse>) result).data;
                if (selfProfile && response != null) {
                    sessionManager.saveSession(
                            sessionManager.getAccessToken(),
                            sessionManager.getRefreshToken(),
                            response.getUserId()
                    );
                }
                ProfileOverview overview = mapToProfileOverview(response);
                if (overview != null) {
                    cacheOverview(overview, selfProfile);
                }
                resultData.setValue(new Result.Success<>(overview));
            } else if (!hasLocal[0]) {
                Result.Error<ProfileOverviewResponse> error = (Result.Error<ProfileOverviewResponse>) result;
                resultData.setValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return resultData;
    }

    // ===== Profile Feature Section =====
    private void cacheOverview(ProfileOverview overview, boolean selfProfile) {
        if (overview == null) {
            return;
        }

        new Thread(() -> profileDao.upsertOverview(mapToProfileOverviewEntity(overview, selfProfile))).start();
    }

    // ===== Profile Feature Section =====
    private void cacheMyInfo(MyProfileInfo info) {
        if (info == null) {
            return;
        }

        new Thread(() -> profileDao.upsertMyInfo(mapToMyProfileInfoEntity(info))).start();
    }

    // ===== Profile Feature Section =====
    private LiveData<Result<List<FollowUser>>> getFollowListOfflineFirst(
            String cacheKey,
            LiveData<Result<FollowListResponse>> networkResult,
            boolean followersTab
    ) {
        MediatorLiveData<Result<List<FollowUser>>> resultData = new MediatorLiveData<>();
        boolean[] hasLocal = { false };

        resultData.addSource(profileDao.getProfileCache(cacheKey), cache -> {
            FollowListResponse response = readProfileCache(cache, FollowListResponse.class);
            if (response != null) {
                hasLocal[0] = true;
                resultData.setValue(new Result.Success<>(mapToFollowUsers(response, followersTab)));
            }
        });

        resultData.addSource(networkResult, result -> {
            if (result instanceof Result.Loading) {
                if (!hasLocal[0]) {
                    resultData.setValue(new Result.Loading<>());
                }
            } else if (result instanceof Result.Success) {
                FollowListResponse response = ((Result.Success<FollowListResponse>) result).data;
                cacheProfileResponse(cacheKey, response);
                resultData.setValue(new Result.Success<>(mapToFollowUsers(response, followersTab)));
            } else if (!hasLocal[0]) {
                Result.Error<FollowListResponse> error = (Result.Error<FollowListResponse>) result;
                resultData.setValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return resultData;
    }

    // ===== Profile Feature Section =====
    private <T> T readProfileCache(ProfileCacheEntity cache, Class<T> responseClass) {
        if (cache == null || cache.json == null) {
            return null;
        }

        try {
            return gson.fromJson(cache.json, responseClass);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    // ===== Profile Feature Section =====
    private void cacheProfileResponse(String cacheKey, Object response) {
        if (response == null) {
            return;
        }

        String json = gson.toJson(response);
        new Thread(() -> profileDao.upsertProfileCache(
                new ProfileCacheEntity(cacheKey, json, System.currentTimeMillis())
        )).start();
    }

    // ===== Profile Feature Section =====
    private String profileFollowCacheKey(int userId, boolean followersTab) {
        return "profile:" + (followersTab ? "followers" : "following") + ":user_" + userId;
    }

    private ProfileOverview mapToProfileOverview(ProfileOverviewResponse response) {
        if (response == null) {
            return null;
        }

        return new ProfileOverview(
                response.getUserId(),
                response.getFullname(),
                response.getAvatarUrl(),
                response.getBio(),
                response.getCity(),
                response.getCountry(),
                response.getTotalFollowings(),
                response.getTotalFollowers(),
                response.isFollowing()
        );
    }

    // ===== Profile Feature Section =====
    private ProfileOverviewEntity mapToProfileOverviewEntity(ProfileOverview overview, boolean selfProfile) {
        return new ProfileOverviewEntity(
                overview.getUserId(),
                selfProfile,
                overview.getFullname(),
                overview.getAvatarUrl(),
                overview.getBio(),
                overview.getCity(),
                overview.getCountry(),
                overview.getTotalFollowings(),
                overview.getTotalFollowers(),
                overview.isFollowing()
        );
    }

    private MyProfileInfo mapToMyProfileInfo(MyProfileInfoPayload payload) {
        if (payload == null) {
            return null;
        }

        return new MyProfileInfo(
                payload.getUsername(),
                payload.getFullname(),
                payload.getEmail(),
                payload.getBirthdate(),
                payload.getAvatarUrl(),
                payload.getBio(),
                payload.getProvinceCity(),
                payload.getCountry(),
                payload.getHeightCm(),
                payload.getWeightKg()
        );
    }

    // ===== Profile Feature Section =====
    private MyProfileInfoEntity mapToMyProfileInfoEntity(MyProfileInfo info) {
        return new MyProfileInfoEntity(
                MY_PROFILE_INFO_ID,
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
        );
    }

    private MyProfileInfoPayload mapToMyProfileInfoPayload(MyProfileInfo profileInfo) {
        if (profileInfo == null) {
            return null;
        }

        return new MyProfileInfoPayload(
                profileInfo.getUsername(),
                profileInfo.getFullname(),
                profileInfo.getEmail(),
                profileInfo.getBirthdate(),
                profileInfo.getAvatarUrl(),
                profileInfo.getBio(),
                profileInfo.getProvinceCity(),
                profileInfo.getCountry(),
                profileInfo.getHeightCm(),
                profileInfo.getWeightKg()
        );
    }

    private List<FollowUser> mapToFollowUsers(FollowListResponse response, boolean followersTab) {
        List<FollowUser> users = new ArrayList<>();
        if (response == null) {
            return users;
        }

        List<FollowUserResponse> source = followersTab ? response.getFollowers() : response.getFollowing();
        if (source == null) {
            return users;
        }

        for (FollowUserResponse item : source) {
            if (item == null) {
                continue;
            }
            users.add(new FollowUser(
                    item.getUserId(),
                    item.getUsername(),
                    item.getFullname(),
                    item.getAvatarUrl(),
                    item.getCreatedAt()
            ));
        }
        return users;
    }
}
