package com.grouprace.core.network.model.auth;

import com.google.gson.annotations.SerializedName;

public class VerifyPasswordResetOtpPayload {
    @SerializedName("email")
    private final String email;

    @SerializedName("otp_code")
    private final String otpCode;

    public VerifyPasswordResetOtpPayload(String email, String otpCode) {
        this.email = email;
        this.otpCode = otpCode;
    }
}
