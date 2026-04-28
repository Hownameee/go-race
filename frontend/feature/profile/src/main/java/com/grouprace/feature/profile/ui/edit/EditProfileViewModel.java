package com.grouprace.feature.profile.ui.edit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.UserRepository;
import com.grouprace.core.model.Profile.MyProfileInfo;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditProfileViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private String currentAvatarUrl;

    @Inject
    public EditProfileViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<Result<MyProfileInfo>> getMyInfo() {
        return userRepository.getMyInfo();
    }

    public void setCurrentAvatarUrl(String avatarUrl) {
        currentAvatarUrl = emptyToNull(avatarUrl);
    }

    public LiveData<Result<String>> uploadMyAvatar(byte[] avatarBytes, String fileName, String mimeType) {
        if (avatarBytes == null || avatarBytes.length == 0) {
            toastMessage.setValue("Please choose an avatar image.");
            return new MutableLiveData<>();
        }

        return userRepository.uploadMyAvatar(avatarBytes, fileName, mimeType);
    }

    public LiveData<Result<Void>> updateMyInfo(
            String username,
            String fullname,
            String birthdate,
            String bio,
            String provinceCity,
            String country,
            String heightText,
            String weightText
    ) {
        if (username.isEmpty() || fullname.isEmpty() || birthdate.isEmpty()) {
            toastMessage.setValue("Please fill in all required fields!");
            return new MutableLiveData<>();
        }

        Double heightCm = parseNullableDouble(heightText, "Height must be a valid number.");
        if (heightCm == INVALID_DOUBLE) {
            return new MutableLiveData<>();
        }

        Double weightKg = parseNullableDouble(weightText, "Weight must be a valid number.");
        if (weightKg == INVALID_DOUBLE) {
            return new MutableLiveData<>();
        }

        MyProfileInfo profileInfo = new MyProfileInfo(
                username,
                fullname,
                null,
                birthdate,
                currentAvatarUrl,
                emptyToNull(bio),
                emptyToNull(provinceCity),
                emptyToNull(country),
                heightCm,
                weightKg
        );

        return userRepository.updateMyInfo(profileInfo);
    }

    private static final Double INVALID_DOUBLE = Double.NaN;

    private Double parseNullableDouble(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException exception) {
            toastMessage.setValue(errorMessage);
            return INVALID_DOUBLE;
        }
    }

    private String emptyToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
