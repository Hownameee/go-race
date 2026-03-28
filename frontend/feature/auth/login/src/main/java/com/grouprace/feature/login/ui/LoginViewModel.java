package com.grouprace.feature.login.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.AuthRepository;
import com.grouprace.core.network.model.auth.LoginPayload;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    private final AuthRepository repository;

    @Inject
    public LoginViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    // Đã đổi thành Result<Void>
    public LiveData<Result<Void>> login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            toastMessage.setValue("Please fill in all required fields!");
            return new MutableLiveData<>();
        }

        LoginPayload payload = new LoginPayload(email, password);

        return repository.login(payload);
    }
}