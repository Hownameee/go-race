package com.grouprace.feature.register.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.grouprace.core.data.repository.AuthRepository;
import com.grouprace.core.network.model.auth.RegisterPayload;
import com.grouprace.core.network.utils.ApiResponse;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RegisterViewModel extends ViewModel {

    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    public LiveData<String> getToastMessage() { return toastMessage; }

    private final AuthRepository repository;

    @Inject
    public RegisterViewModel(AuthRepository authRepository) {
        this.repository = authRepository;
    }

    // Trả thẳng LiveData về cho Fragment observe
    public LiveData<ApiResponse<JsonObject>> register(String username, String fullname, String email, String birthdate, String password, String confirmPassword) {
        if (username.isEmpty() || fullname.isEmpty() || email.isEmpty() ||
                birthdate.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            toastMessage.setValue("Please fill in all required fields!");
            // Trả về một LiveData rỗng để ngăn luồng chạy tiếp
            return new MutableLiveData<>();
        }

        if (!password.equals(confirmPassword)) {
            toastMessage.setValue("Passwords do not match!");
            return new MutableLiveData<>();
        }

        RegisterPayload payload = new RegisterPayload(username, fullname, email, birthdate, password);
        return repository.register(payload);
    }
}