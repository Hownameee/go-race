package com.grouprace.core.network.model.auth;

import com.google.gson.annotations.SerializedName;

public class RequestPasswordResetOtpPayload {
    @SerializedName("email")
    private final String email;

    public RequestPasswordResetOtpPayload(String email) {
        this.email = email;
    }
}
