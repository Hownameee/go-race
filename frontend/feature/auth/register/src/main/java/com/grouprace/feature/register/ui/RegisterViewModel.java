package com.grouprace.feature.register.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.AuthRepository;
import com.grouprace.core.network.model.auth.GoogleAuthPayload;
import com.grouprace.core.network.model.auth.GoogleAuthResponse;
import com.grouprace.core.network.model.auth.RegisterPayload;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RegisterViewModel extends ViewModel {

    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MediatorLiveData<Result<GoogleAuthResponse>> googleAuthState = new MediatorLiveData<>();

    public LiveData<String> getToastMessage() { return toastMessage; }
    public LiveData<Result<GoogleAuthResponse>> getGoogleAuthState() { return googleAuthState; }

    private final AuthRepository repository;

    @Inject
    public RegisterViewModel(AuthRepository authRepository) {
        this.repository = authRepository;
    }

    public LiveData<Result<Void>> register(String username, String fullname, String email, String birthdate, String password, String confirmPassword) {
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

    public void onGoogleIdTokenReceived(String idToken, String username, String birthdate) {
        if (idToken == null || idToken.isEmpty()) {
            googleAuthState.setValue(
                    new Result.Error<>(new IllegalArgumentException("Missing Google ID token"),
                            "Google ID token is missing!")
            );
            return;
        }

        GoogleAuthPayload payload =
                (username == null || username.isEmpty() || birthdate == null || birthdate.isEmpty())
                        ? new GoogleAuthPayload(idToken)
                        : new GoogleAuthPayload(idToken, username, birthdate);

        LiveData<Result<GoogleAuthResponse>> source =
                repository.googleAuth(payload);

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
