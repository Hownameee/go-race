package com.grouprace.core.network.model.user;

import com.google.gson.annotations.SerializedName;

public class RequestEmailOtpPayload {
    @SerializedName("new_email")
    private String newEmail;

    public RequestEmailOtpPayload(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }
}
