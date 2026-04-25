package com.grouprace.core.network.model.auth;

public class RefreshTokenPayload {
    private final String refresh_token;

    public RefreshTokenPayload(String refreshToken) {
        this.refresh_token = refreshToken;
    }

    public String getRefresh_token() {
        return refresh_token;
    }
}
