package com.grouprace.core.network.model.user;

import com.google.gson.annotations.SerializedName;

public class AvatarUploadResponse {
    @SerializedName("avatar_url")
    private String avatarUrl;

    public AvatarUploadResponse() {}

    public AvatarUploadResponse(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
