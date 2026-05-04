package com.grouprace.feature.profile.ui.settings.email;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.common.validation.FormValidator;
import com.grouprace.core.data.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChangeEmailViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private boolean otpRequested;
    private boolean currentEmailVerified;
    private boolean newEmailOtpRequested;
    private String pendingNewEmail;

    @Inject
    public ChangeEmailViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public boolean isCurrentEmailVerified() {
        return currentEmailVerified;
    }

    public void markCurrentEmailVerified() {
        currentEmailVerified = true;
    }

    public void resetFlow() {
        otpRequested = false;
        currentEmailVerified = false;
        newEmailOtpRequested = false;
        pendingNewEmail = null;
    }

    public boolean isOtpRequested() {
        return otpRequested;
    }

    public LiveData<Result<Void>> requestOtp() {
        return userRepository.requestEmailChangeOtp();
    }

    public void markOtpRequested() {
        otpRequested = true;
    }

    public boolean isNewEmailOtpRequested() {
        return newEmailOtpRequested;
    }

    public String getPendingNewEmail() {
        return pendingNewEmail;
    }

    public LiveData<Result<Void>> verifyOtp(String otpCode) {
        String otpError = FormValidator.getOtpError(otpCode);
        if (otpError != null) {
            toastMessage.setValue(otpError);
            return new MutableLiveData<>();
        }

        return userRepository.verifyEmailChangeOtp(otpCode.trim());
    }

    public LiveData<Result<Void>> requestNewEmailOtp(String newEmail) {
        if (!currentEmailVerified) {
            toastMessage.setValue("Please verify the OTP sent to your current email first.");
            return new MutableLiveData<>();
        }

        String emailError = FormValidator.getEmailError(newEmail);
        if (emailError != null) {
            toastMessage.setValue(emailError);
            return new MutableLiveData<>();
        }

        pendingNewEmail = newEmail.trim();
        return userRepository.requestNewEmailChangeOtp(pendingNewEmail);
    }

    public void markNewEmailOtpRequested(String newEmail) {
        pendingNewEmail = newEmail != null ? newEmail.trim() : null;
        newEmailOtpRequested = true;
    }

    public LiveData<Result<Void>> confirmChange(String newEmail, String otpCode) {
        if (!currentEmailVerified) {
            toastMessage.setValue("Please verify the OTP sent to your current email first.");
            return new MutableLiveData<>();
        }

        String emailError = FormValidator.getEmailError(newEmail);
        if (emailError != null) {
            toastMessage.setValue(emailError);
            return new MutableLiveData<>();
        }

        String otpError = FormValidator.getOtpError(otpCode);
        if (otpError != null) {
            toastMessage.setValue(otpError);
            return new MutableLiveData<>();
        }

        pendingNewEmail = newEmail.trim();
        return userRepository.confirmEmailChange(pendingNewEmail, otpCode.trim());
    }
}
