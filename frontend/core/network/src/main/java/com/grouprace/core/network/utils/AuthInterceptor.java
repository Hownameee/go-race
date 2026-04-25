package com.grouprace.core.network.utils;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private static final String REFRESH_PATH = "/api/auth/refresh-token";
    private static final String BASE_URL = "http://192.168.2.105:5000";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final SessionManager sessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String accessToken = sessionManager.getAccessToken();

        Request requestWithToken = originalRequest;
        if (accessToken != null && !accessToken.isEmpty()) {
            requestWithToken = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
        }

        Response response = chain.proceed(requestWithToken);

        if ((response.code() != 401 && response.code() != 403)
                || originalRequest.url().encodedPath().endsWith(REFRESH_PATH)) {
            return response;
        }

        String refreshToken = sessionManager.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            sessionManager.clearSession();
            return response;
        }

        LoginTokens refreshedTokens = refreshTokens(refreshToken);
        if (refreshedTokens == null) {
            sessionManager.clearSession();
            return response;
        }

        sessionManager.saveSession(refreshedTokens.accessToken, refreshedTokens.refreshToken);
        response.close();

        Request retriedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + refreshedTokens.accessToken)
                .build();
        return chain.proceed(retriedRequest);
    }

    private LoginTokens refreshTokens(String refreshToken) {
        OkHttpClient refreshClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        try {
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("refresh_token", refreshToken);

            Request refreshRequest = new Request.Builder()
                    .url(BASE_URL + REFRESH_PATH)
                    .post(RequestBody.create(bodyJson.toString(), JSON))
                    .build();

            try (Response refreshResponse = refreshClient.newCall(refreshRequest).execute()) {
                if (!refreshResponse.isSuccessful() || refreshResponse.body() == null) {
                    return null;
                }

                JSONObject json = new JSONObject(refreshResponse.body().string());
                JSONObject data = json.optJSONObject("data");
                if (data == null) {
                    return null;
                }

                String nextAccessToken = data.optString("access_token", null);
                String nextRefreshToken = data.optString("refresh_token", null);
                if (nextAccessToken == null || nextAccessToken.isEmpty()
                        || nextRefreshToken == null || nextRefreshToken.isEmpty()) {
                    return null;
                }

                return new LoginTokens(nextAccessToken, nextRefreshToken);
            }
        } catch (Exception exception) {
            return null;
        }
    }

    private static class LoginTokens {
        final String accessToken;
        final String refreshToken;

        LoginTokens(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
