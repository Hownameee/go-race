package com.grouprace.core.network.model.user;

import com.google.gson.annotations.SerializedName;

public class ConfirmEmailChangePayload {
    @SerializedName("new_email")
    private String newEmail;

    @SerializedName("otp_code")
    private String otpCode;

    public ConfirmEmailChangePayload(String newEmail, String otpCode) {
        this.newEmail = newEmail;
        this.otpCode = otpCode;
    }
}
