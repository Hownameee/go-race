package com.grouprace.core.network.model.user;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordWithOtpPayload {
    @SerializedName("email")
    private String email;

    @SerializedName("otp_code")
    private String otpCode;

    @SerializedName("new_password")
    private String newPassword;

    @SerializedName("confirm_new_password")
    private String confirmNewPassword;

    public ResetPasswordWithOtpPayload(String otpCode, String newPassword, String confirmNewPassword) {
        this(null, otpCode, newPassword, confirmNewPassword);
    }

    public ResetPasswordWithOtpPayload(String email, String otpCode, String newPassword, String confirmNewPassword) {
        this.email = email;
        this.otpCode = otpCode;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }
}
