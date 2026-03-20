package com.grouprace.core.network.model.authentication;

import androidx.annotation.NonNull;

public class LoginPayload {
    private String email;
    private String password;

    public LoginPayload(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @NonNull
    @Override
    public String toString() {
        return "LoginPayload{" +
                "email='" + email + '\'' +
                ", password='[BẢO MẬT]'" +
                '}';
    }
}