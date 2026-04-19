package com.grouprace.core.network.model.user;

import com.google.gson.annotations.SerializedName;

public class VerifyEmailOtpPayload {
    @SerializedName("otp_code")
    private String otpCode;

    public VerifyEmailOtpPayload(String otpCode) {
        this.otpCode = otpCode;
    }
}
