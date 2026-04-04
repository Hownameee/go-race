package com.grouprace.feature.profile.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileSettingsViewModel extends ViewModel {
    private final UserRepository userRepository;

    @Inject
    public ProfileSettingsViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Result<Void>> deleteMyAccount() {
        return userRepository.deleteMyAccount();
    }
}
