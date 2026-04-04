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
    private String pendingNewEmail;

    @Inject
    public ChangeEmailViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public void setPendingNewEmail(String newEmail) {
        pendingNewEmail = newEmail != null ? newEmail.trim() : null;
    }

    public String getPendingNewEmail() {
        return pendingNewEmail;
    }

    public void clearPendingNewEmail() {
        pendingNewEmail = null;
    }

    public LiveData<Result<Void>> requestOtp(String newEmail) {
        if (newEmail == null || newEmail.trim().isEmpty()) {
            toastMessage.setValue("Please enter a new email.");
            return new MutableLiveData<>();
        }

        pendingNewEmail = newEmail.trim();
        return userRepository.requestEmailChangeOtp(pendingNewEmail);
    }

    public LiveData<Result<Void>> confirmChange(String otpCode) {
        if (pendingNewEmail == null || pendingNewEmail.trim().isEmpty()) {
            toastMessage.setValue("Missing new email information.");
            return new MutableLiveData<>();
        }

        if (otpCode == null || otpCode.trim().isEmpty()) {
            toastMessage.setValue("Please enter OTP.");
            return new MutableLiveData<>();
        }

        return userRepository.confirmEmailChange(pendingNewEmail, otpCode.trim());
    }
}
