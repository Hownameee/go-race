package com.grouprace.core.network.model.notification;

import com.google.gson.annotations.SerializedName;

public class RegisterDeviceTokenRequest {
    @SerializedName("token")
    private final String token;

    @SerializedName("platform")
    private final String platform;

    public RegisterDeviceTokenRequest(String token, String platform) {
        this.token = token;
        this.platform = platform;
    }
}

