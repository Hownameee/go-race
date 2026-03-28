package com.grouprace.core.network.model.auth;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;

    public LoginResponse() {}

    public String getToken() { return token; }
    public  void setToken(String token) { this.token = token; }
}
