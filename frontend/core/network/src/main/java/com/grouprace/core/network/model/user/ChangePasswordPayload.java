package com.grouprace.core.network.model.user;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordPayload {
    @SerializedName("old_password")
    private String oldPassword;

    @SerializedName("new_password")
    private String newPassword;

    @SerializedName("confirm_new_password")
    private String confirmNewPassword;

    public ChangePasswordPayload(String oldPassword, String newPassword, String confirmNewPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }
}
