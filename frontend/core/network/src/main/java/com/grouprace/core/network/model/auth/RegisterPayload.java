package com.grouprace.core.network.model.auth;

import androidx.annotation.NonNull;

public class RegisterPayload {
    private String username;
    private String fullname;
    private String email;
    private String password;
    private String birthdate;

    public RegisterPayload(String username, String fullname, String email, String birthdate, String password) {
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
    }

    @NonNull
    @Override
    public String toString() {
        return "RegisterPayload{" +
                "username='" + username + '\'' +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", password='[BẢO MẬT]'" +
                '}';
    }
}
