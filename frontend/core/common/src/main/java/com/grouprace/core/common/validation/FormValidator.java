package com.grouprace.core.common.validation;

import android.util.Patterns;

import androidx.annotation.Nullable;

import java.util.regex.Pattern;

public final class FormValidator {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    private static final Pattern BIRTHDATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern STRONG_PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$");
    private static final Pattern OTP_PATTERN = Pattern.compile("^\\d{6}$");

    private FormValidator() {
    }

    public static boolean isBlank(@Nullable String value) {
        return value == null || value.trim().isEmpty();
    }

    @Nullable
    public static String getUsernameError(@Nullable String username) {
        if (isBlank(username)) {
            return "Username is required.";
        }
        String normalized = username.trim();
        if (!USERNAME_PATTERN.matcher(normalized).matches()) {
            return "Username must be 3-50 characters and contain only letters, numbers, and underscores.";
        }
        return null;
    }

    @Nullable
    public static String getFullnameError(@Nullable String fullname) {
        if (isBlank(fullname)) {
            return "Full name cannot be empty.";
        }
        if (fullname.trim().length() > 100) {
            return "Full name is too long.";
        }
        return null;
    }

    @Nullable
    public static String getEmailError(@Nullable String email) {
        if (isBlank(email)) {
            return "Email is required.";
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return "Invalid email format.";
        }
        return null;
    }

    @Nullable
    public static String getBirthdateError(@Nullable String birthdate) {
        if (isBlank(birthdate)) {
            return "Birthdate is required.";
        }
        if (!BIRTHDATE_PATTERN.matcher(birthdate.trim()).matches()) {
            return "Birthdate must be in YYYY-MM-DD format.";
        }
        return null;
    }

    @Nullable
    public static String getPasswordError(@Nullable String password) {
        if (isBlank(password)) {
            return "Password is required.";
        }
        if (!STRONG_PASSWORD_PATTERN.matcher(password.trim()).matches()) {
            return "Password must be at least 8 characters and include uppercase, lowercase, numbers, and special characters.";
        }
        return null;
    }

    @Nullable
    public static String getOtpError(@Nullable String otpCode) {
        if (isBlank(otpCode)) {
            return "OTP is required.";
        }
        if (!OTP_PATTERN.matcher(otpCode.trim()).matches()) {
            return "OTP must be 6 digits.";
        }
        return null;
    }
}
