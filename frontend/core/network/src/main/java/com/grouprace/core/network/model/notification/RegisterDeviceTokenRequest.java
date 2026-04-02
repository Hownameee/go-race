package com.grouprace.core.network.model.notification;

import com.google.gson.annotations.SerializedName;

public class RegisterDeviceTokenRequest {
    @SerializedName("user_id")
    private final int userId;

    @SerializedName("token")
    private final String token;

    @SerializedName("platform")
    private final String platform;

    public RegisterDeviceTokenRequest(int userId, String token, String platform) {
        this.userId = userId;
        this.token = token;
        this.platform = platform;
    }
}

