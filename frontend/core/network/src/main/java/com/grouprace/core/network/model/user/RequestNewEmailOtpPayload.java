package com.grouprace.core.network.model.user;

import com.google.gson.annotations.SerializedName;

public class RequestNewEmailOtpPayload {
    @SerializedName("new_email")
    private final String newEmail;

    public RequestNewEmailOtpPayload(String newEmail) {
        this.newEmail = newEmail;
    }
}
