package com.grouprace.core.network.utils;

import androidx.annotation.NonNull;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import com.grouprace.core.network.utils.SessionManager;

public class AuthInterceptor implements Interceptor {

    private final SessionManager sessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String token = sessionManager.getAuthToken();
        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjQsInJvbGUiOiJ1c2VyIiwiaWF0IjoxNzc0NjkwNjA4LCJleHAiOjE3NzQ2OTQyMDh9.Ql83hhsMwVpy8tZqUsc-Kh9jjIJTRMObDPRmPa5F6Vw";

        if (token != null) {

            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();

            return chain.proceed(newRequest);
        }

        return chain.proceed(originalRequest);
    }
}