package com.grouprace.core.network.model.user;

import com.google.gson.annotations.SerializedName;

public class VerifyCurrentPasswordPayload {
    @SerializedName("old_password")
    private final String oldPassword;

    public VerifyCurrentPasswordPayload(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
