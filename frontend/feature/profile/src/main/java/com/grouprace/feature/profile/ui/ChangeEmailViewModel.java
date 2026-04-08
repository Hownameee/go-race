package com.grouprace.feature.profile.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChangeEmailViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private boolean otpRequested;
    private boolean currentEmailVerified;

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

    public LiveData<Result<Void>> verifyOtp(String otpCode) {
        if (otpCode == null || otpCode.trim().isEmpty()) {
            toastMessage.setValue("Please enter OTP.");
            return new MutableLiveData<>();
        }

        return userRepository.verifyEmailChangeOtp(otpCode.trim());
    }

    public LiveData<Result<Void>> confirmChange(String newEmail) {
        if (!currentEmailVerified) {
            toastMessage.setValue("Please verify the OTP sent to your current email first.");
            return new MutableLiveData<>();
        }

        if (newEmail == null || newEmail.trim().isEmpty()) {
            toastMessage.setValue("Please enter a new email.");
            return new MutableLiveData<>();
        }

        return userRepository.confirmEmailChange(newEmail.trim());
    }
}
