package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.MyProfileInfo;
import com.grouprace.core.model.Profile.ProfileOverview;
import com.grouprace.core.network.model.user.MyProfileInfoPayload;
import com.grouprace.core.network.model.user.ProfileOverviewResponse;
import com.grouprace.core.network.source.UserDataSource;

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
                response.getCity(),
                response.getCountry(),
                response.getTotalFollowings(),
                response.getTotalFollowers()
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
                payload.getNationality(),
                payload.getAddress(),
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
                profileInfo.getNationality(),
                profileInfo.getAddress(),
                profileInfo.getHeightCm(),
                profileInfo.getWeightKg()
        );
    }
}
