package com.grouprace.feature.profile.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.AuthRepository;
import com.grouprace.core.data.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChangePasswordViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private String verifiedCurrentPassword;
    private String resetEmail;
    private String verifiedResetOtp;

    @Inject
    public ChangePasswordViewModel(UserRepository userRepository, AuthRepository authRepository) {
        this.userRepository = userRepository;
        this.authRepository = authRepository;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public void setVerifiedCurrentPassword(String currentPassword) {
        verifiedCurrentPassword = currentPassword != null ? currentPassword.trim() : null;
        resetEmail = null;
        verifiedResetOtp = null;
    }

    public void setResetEmail(String email) {
        resetEmail = email != null ? email.trim() : null;
    }

    public String getResetEmail() {
        return resetEmail;
    }

    public void setVerifiedResetOtp(String otpCode) {
        verifiedResetOtp = otpCode != null ? otpCode.trim() : null;
        verifiedCurrentPassword = null;
    }

    public LiveData<Result<Void>> verifyCurrentPassword(String currentPassword) {
        if (isBlank(currentPassword)) {
            toastMessage.setValue("Please enter your current password.");
            return new MutableLiveData<>();
        }

        return userRepository.verifyCurrentPassword(currentPassword.trim());
    }

    public LiveData<Result<Void>> requestPasswordResetOtp(String email) {
        if (isBlank(email)) {
            toastMessage.setValue("Please enter your email.");
            return new MutableLiveData<>();
        }

        resetEmail = email.trim();
        return authRepository.requestPasswordResetOtp(resetEmail);
    }

    public LiveData<Result<Void>> verifyResetOtp(String otpCode) {
        if (isBlank(resetEmail)) {
            toastMessage.setValue("Missing reset email.");
            return new MutableLiveData<>();
        }

        if (isBlank(otpCode)) {
            toastMessage.setValue("Please enter OTP.");
            return new MutableLiveData<>();
        }

        return authRepository.verifyPasswordResetOtp(resetEmail, otpCode.trim());
    }

    public LiveData<Result<Void>> submitNewPassword(String newPassword, String confirmPassword) {
        if (isBlank(newPassword) || isBlank(confirmPassword)) {
            toastMessage.setValue("Please fill in new password fields.");
            return new MutableLiveData<>();
        }

        if (!isBlank(verifiedCurrentPassword)) {
            return userRepository.changePassword(
                    verifiedCurrentPassword,
                    newPassword.trim(),
                    confirmPassword.trim()
            );
        }

        if (isBlank(resetEmail) || isBlank(verifiedResetOtp)) {
            toastMessage.setValue("Missing password reset verification.");
            return new MutableLiveData<>();
        }

        return authRepository.resetPasswordWithOtp(
                resetEmail,
                verifiedResetOtp,
                newPassword.trim(),
                confirmPassword.trim()
        );
    }

    public void clearFlowState() {
        verifiedCurrentPassword = null;
        resetEmail = null;
        verifiedResetOtp = null;
    }

    public LiveData<Result<Void>> legacyResetPasswordWithOtp(String otpCode, String newPassword, String confirmPassword) {
        return userRepository.resetPasswordWithOtp(
                otpCode.trim(),
                newPassword.trim(),
                confirmPassword.trim()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
