package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.FollowUser;
import com.grouprace.core.model.Profile.MyProfileInfo;
import com.grouprace.core.model.Profile.ProfileOverview;
import com.grouprace.core.network.model.user.FollowListResponse;
import com.grouprace.core.network.model.user.FollowUserResponse;
import com.grouprace.core.network.model.user.MyProfileInfoPayload;
import com.grouprace.core.network.model.user.ProfileOverviewResponse;
import com.grouprace.core.network.source.UserDataSource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class UserRepositoryImpl implements UserRepository {

    private final UserDataSource userDataSource;

    @Inject
    public UserRepositoryImpl(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    @Override
    public LiveData<Result<ProfileOverview>> getMyOverview() {
        LiveData<Result<ProfileOverviewResponse>> networkResult = userDataSource.getMyOverview();

        return Transformations.map(networkResult, result -> {
            if (result instanceof Result.Loading) {
                return new Result.Loading<>();
            } else if (result instanceof Result.Success) {
                ProfileOverviewResponse response = ((Result.Success<ProfileOverviewResponse>) result).data;
                return new Result.Success<>(mapToProfileOverview(response));
            } else {
                Result.Error<ProfileOverviewResponse> error = (Result.Error<ProfileOverviewResponse>) result;
                return new Result.Error<>(error.exception, error.message);
            }
        });
    }

    @Override
    public LiveData<Result<ProfileOverview>> getUserOverview(int userId) {
        LiveData<Result<ProfileOverviewResponse>> networkResult = userDataSource.getUserOverview(userId);

        return Transformations.map(networkResult, result -> {
            if (result instanceof Result.Loading) {
                return new Result.Loading<>();
            } else if (result instanceof Result.Success) {
                ProfileOverviewResponse response = ((Result.Success<ProfileOverviewResponse>) result).data;
                return new Result.Success<>(mapToProfileOverview(response));
            } else {
                Result.Error<ProfileOverviewResponse> error = (Result.Error<ProfileOverviewResponse>) result;
                return new Result.Error<>(error.exception, error.message);
            }
        });
    }

    @Override
    public LiveData<Result<List<FollowUser>>> getFollowers(int userId) {
        LiveData<Result<FollowListResponse>> networkResult = userDataSource.getFollowers(userId);

        return Transformations.map(networkResult, result -> mapFollowListResult(result, true));
    }

    @Override
    public LiveData<Result<List<FollowUser>>> getFollowing(int userId) {
        LiveData<Result<FollowListResponse>> networkResult = userDataSource.getFollowing(userId);

        return Transformations.map(networkResult, result -> mapFollowListResult(result, false));
    }

    @Override
    public LiveData<Result<MyProfileInfo>> getMyInfo() {
        LiveData<Result<MyProfileInfoPayload>> networkResult = userDataSource.getMyInfo();

        return Transformations.map(networkResult, result -> {
            if (result instanceof Result.Loading) {
                return new Result.Loading<>();
            } else if (result instanceof Result.Success) {
                MyProfileInfoPayload response = ((Result.Success<MyProfileInfoPayload>) result).data;
                return new Result.Success<>(mapToMyProfileInfo(response));
            } else {
                Result.Error<MyProfileInfoPayload> error = (Result.Error<MyProfileInfoPayload>) result;
                return new Result.Error<>(error.exception, error.message);
            }
        });
    }

    @Override
    public LiveData<Result<Void>> requestEmailChangeOtp() {
        return userDataSource.requestEmailChangeOtp();
    }

    @Override
    public LiveData<Result<Void>> verifyEmailChangeOtp(String otpCode) {
        return userDataSource.verifyEmailChangeOtp(otpCode);
    }

    @Override
    public LiveData<Result<Void>> requestNewEmailChangeOtp(String newEmail) {
        return userDataSource.requestNewEmailChangeOtp(newEmail);
    }

    @Override
    public LiveData<Result<Void>> confirmEmailChange(String newEmail, String otpCode) {
        return userDataSource.confirmEmailChange(newEmail, otpCode);
    }

    @Override
    public LiveData<Result<Void>> verifyCurrentPassword(String oldPassword) {
        return userDataSource.verifyCurrentPassword(oldPassword);
    }

    @Override
    public LiveData<Result<Void>> changePassword(String oldPassword, String newPassword, String confirmNewPassword) {
        return userDataSource.changePassword(oldPassword, newPassword, confirmNewPassword);
    }

    @Override
    public LiveData<Result<Void>> requestPasswordResetOtp() {
        return userDataSource.requestPasswordResetOtp();
    }

    @Override
    public LiveData<Result<Void>> resetPasswordWithOtp(String otpCode, String newPassword, String confirmNewPassword) {
        return userDataSource.resetPasswordWithOtp(otpCode, newPassword, confirmNewPassword);
    }

    @Override
    public LiveData<Result<Void>> deleteMyAccount() {
        return userDataSource.deleteMyAccount();
    }

    @Override
    public LiveData<Result<String>> uploadMyAvatar(byte[] avatarBytes, String fileName, String mimeType) {
        return userDataSource.uploadMyAvatar(avatarBytes, fileName, mimeType);
    }

    @Override
    public LiveData<Result<Void>> updateMyInfo(MyProfileInfo myProfileInfo) {
        return userDataSource.updateMyInfo(mapToMyProfileInfoPayload(myProfileInfo));
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

    private Result<List<FollowUser>> mapFollowListResult(Result<FollowListResponse> result, boolean followersTab) {
        if (result instanceof Result.Loading) {
            return new Result.Loading<>();
        } else if (result instanceof Result.Success) {
            FollowListResponse response = ((Result.Success<FollowListResponse>) result).data;
            return new Result.Success<>(mapToFollowUsers(response, followersTab));
        } else {
            Result.Error<FollowListResponse> error = (Result.Error<FollowListResponse>) result;
            return new Result.Error<>(error.exception, error.message);
        }
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
