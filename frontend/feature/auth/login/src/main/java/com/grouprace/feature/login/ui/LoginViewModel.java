package com.grouprace.feature.login.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.common.validation.FormValidator;
import com.grouprace.core.data.repository.AuthRepository;
import com.grouprace.core.network.model.auth.GoogleAuthPayload;
import com.grouprace.core.network.model.auth.GoogleAuthResponse;
import com.grouprace.core.network.model.auth.LoginPayload;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MediatorLiveData<Result<GoogleAuthResponse>> googleAuthState = new MediatorLiveData<>();
    private final AuthRepository repository;

    @Inject
    public LoginViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<Result<GoogleAuthResponse>> getGoogleAuthState() {
        return googleAuthState;
    }

    public LiveData<Result<Void>> login(String email, String password) {
        String emailError = FormValidator.getEmailError(email);
        if (emailError != null) {
            toastMessage.setValue(emailError);
            return new MutableLiveData<>();
        }

        if (FormValidator.isBlank(password)) {
            toastMessage.setValue("Password is required.");
            return new MutableLiveData<>();
        }

        LoginPayload payload = new LoginPayload(email.trim(), password.trim());
        return repository.login(payload);
    }

    public void onGoogleIdTokenReceived(String idToken) {
        if (FormValidator.isBlank(idToken)) {
            googleAuthState.setValue(
                    new Result.Error<>(
                            new IllegalArgumentException("Missing Google ID token"),
                            "Google ID token is missing!"
                    )
            );
            return;
        }

        LiveData<Result<GoogleAuthResponse>> source =
                repository.googleAuth(new GoogleAuthPayload(idToken));

        googleAuthState.addSource(source, result -> {
            googleAuthState.setValue(result);
            if (!(result instanceof Result.Loading)) {
                googleAuthState.removeSource(source);
            }
        });
    }

    public LiveData<Result<Boolean>> registerDeviceToken(String token) {
        return repository.registerDeviceToken(token);
    }
}
