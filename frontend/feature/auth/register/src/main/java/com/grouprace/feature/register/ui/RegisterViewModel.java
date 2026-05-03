package com.grouprace.feature.register.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.common.validation.FormValidator;
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
    private final AuthRepository repository;

    @Inject
    public RegisterViewModel(AuthRepository authRepository) {
        this.repository = authRepository;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<Result<GoogleAuthResponse>> getGoogleAuthState() {
        return googleAuthState;
    }

    public LiveData<Result<Void>> register(
            String username,
            String fullname,
            String email,
            String birthdate,
            String password,
            String confirmPassword
    ) {
        String usernameError = FormValidator.getUsernameError(username);
        if (usernameError != null) {
            toastMessage.setValue(usernameError);
            return new MutableLiveData<>();
        }

        String fullnameError = FormValidator.getFullnameError(fullname);
        if (fullnameError != null) {
            toastMessage.setValue(fullnameError);
            return new MutableLiveData<>();
        }

        String emailError = FormValidator.getEmailError(email);
        if (emailError != null) {
            toastMessage.setValue(emailError);
            return new MutableLiveData<>();
        }

        String birthdateError = FormValidator.getBirthdateError(birthdate);
        if (birthdateError != null) {
            toastMessage.setValue(birthdateError);
            return new MutableLiveData<>();
        }

        String passwordError = FormValidator.getPasswordError(password);
        if (passwordError != null) {
            toastMessage.setValue(passwordError);
            return new MutableLiveData<>();
        }

        if (FormValidator.isBlank(confirmPassword)) {
            toastMessage.setValue("Please confirm your password.");
            return new MutableLiveData<>();
        }

        if (!password.trim().equals(confirmPassword.trim())) {
            toastMessage.setValue("Confirm password does not match.");
            return new MutableLiveData<>();
        }

        RegisterPayload payload = new RegisterPayload(
                username.trim(),
                fullname.trim(),
                email.trim(),
                birthdate.trim(),
                password.trim()
        );
        return repository.register(payload);
    }

    public void onGoogleIdTokenReceived(String idToken, String username, String birthdate) {
        if (FormValidator.isBlank(idToken)) {
            googleAuthState.setValue(
                    new Result.Error<>(
                            new IllegalArgumentException("Missing Google ID token"),
                            "Google ID token is missing!"
                    )
            );
            return;
        }

        if (!FormValidator.isBlank(username)) {
            String usernameError = FormValidator.getUsernameError(username);
            if (usernameError != null) {
                googleAuthState.setValue(
                        new Result.Error<>(new IllegalArgumentException(usernameError), usernameError)
                );
                return;
            }
        }

        if (!FormValidator.isBlank(birthdate)) {
            String birthdateError = FormValidator.getBirthdateError(birthdate);
            if (birthdateError != null) {
                googleAuthState.setValue(
                        new Result.Error<>(new IllegalArgumentException(birthdateError), birthdateError)
                );
                return;
            }
        }

        GoogleAuthPayload payload =
                (FormValidator.isBlank(username) || FormValidator.isBlank(birthdate))
                        ? new GoogleAuthPayload(idToken)
                        : new GoogleAuthPayload(idToken, username.trim(), birthdate.trim());

        LiveData<Result<GoogleAuthResponse>> source = repository.googleAuth(payload);
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
