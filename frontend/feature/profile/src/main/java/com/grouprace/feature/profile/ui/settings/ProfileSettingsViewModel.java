package com.grouprace.feature.profile.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.AuthRepository;
import com.grouprace.core.data.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileSettingsViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    @Inject
    public ProfileSettingsViewModel(UserRepository userRepository, AuthRepository authRepository) {
        this.userRepository = userRepository;
        this.authRepository = authRepository;
    }

    public void logout() {
        authRepository.logout();
    }

    public LiveData<Result<Boolean>> unregisterDeviceToken(String token) {
        return authRepository.unregisterDeviceToken(token);
    }

    public LiveData<Result<Void>> deleteMyAccount() {
        return userRepository.deleteMyAccount();
    }
}
