package com.grouprace.feature.login.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.grouprace.core.data.repository.AuthenticationRepository;
import com.grouprace.core.network.model.authentication.LoginPayload;
import com.grouprace.core.network.utils.ApiResponse;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    private final AuthenticationRepository repository;

    @Inject
    public LoginViewModel(AuthenticationRepository repository) {
        this.repository = repository;
    }

    // Đổi kiểu trả về thành LiveData
    public LiveData<ApiResponse<JsonObject>> login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            toastMessage.setValue("Please fill in all required fields!");
            return new MutableLiveData<>(); // Trả về LiveData rỗng để ngắt luồng
        }

        LoginPayload payload = new LoginPayload(email, password);

        return repository.login(payload);
    }
}