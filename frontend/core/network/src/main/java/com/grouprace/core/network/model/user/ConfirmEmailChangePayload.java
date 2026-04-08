package com.grouprace.core.network.model.user;

import com.google.gson.annotations.SerializedName;

public class ConfirmEmailChangePayload {
    @SerializedName("new_email")
    private String newEmail;

    public ConfirmEmailChangePayload(String newEmail) {
        this.newEmail = newEmail;
    }
}
