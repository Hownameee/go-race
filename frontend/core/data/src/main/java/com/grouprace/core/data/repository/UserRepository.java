package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.MyProfileInfo;
import com.grouprace.core.model.Profile.ProfileOverview;

public interface UserRepository {
    LiveData<Result<ProfileOverview>> getMyOverview();
    LiveData<Result<MyProfileInfo>> getMyInfo();
    LiveData<Result<Void>> requestEmailChangeOtp();
    LiveData<Result<Void>> verifyEmailChangeOtp(String otpCode);
    LiveData<Result<Void>> requestNewEmailChangeOtp(String newEmail);
    LiveData<Result<Void>> confirmEmailChange(String newEmail, String otpCode);
    LiveData<Result<Void>> verifyCurrentPassword(String oldPassword);
    LiveData<Result<Void>> changePassword(String oldPassword, String newPassword, String confirmNewPassword);
    LiveData<Result<Void>> requestPasswordResetOtp();
    LiveData<Result<Void>> resetPasswordWithOtp(String otpCode, String newPassword, String confirmNewPassword);
    LiveData<Result<Void>> deleteMyAccount();
    LiveData<Result<String>> uploadMyAvatar(byte[] avatarBytes, String fileName, String mimeType);
    LiveData<Result<Void>> updateMyInfo(MyProfileInfo myProfileInfo);
}
