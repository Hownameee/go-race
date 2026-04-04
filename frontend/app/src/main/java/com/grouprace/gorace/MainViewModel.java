package com.grouprace.gorace;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.data.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final AuthRepository authRepository;

    @Inject
    public MainViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return authRepository.getIsLoggedIn();
    }

    public void logout() {
        authRepository.logout();
    }
}
