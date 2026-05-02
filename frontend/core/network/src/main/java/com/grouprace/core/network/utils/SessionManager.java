package com.grouprace.core.network.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class SessionManager {
    private static final String PREF_NAME = "GoRaceApp";
    private static final String KEY_ACCESS_TOKEN = "Access_Token";
    private static final String KEY_REFRESH_TOKEN = "Refresh_Token";
    private static final String KEY_USER_ID = "User_Id";

    private final SharedPreferences prefs;
    private final MutableLiveData<Boolean> loginState = new MutableLiveData<>();

    @Inject
    public SessionManager(@ApplicationContext Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.loginState.setValue(hasValidSession());
    }

    public void saveSession(String accessToken, String refreshToken) {
        saveSession(accessToken, refreshToken, -1);
    }

    public void saveSession(String accessToken, String refreshToken, int userId) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putInt(KEY_USER_ID, userId)
                .commit();
        loginState.postValue(true);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, 0);
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public void clearSession() {
        prefs.edit()
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .commit();
        loginState.postValue(false);
    }

    public boolean isLoggedIn() {
        return hasValidSession();
    }

    public LiveData<Boolean> getLoginState() {
        return loginState;
    }

    private boolean hasValidSession() {
        String accessToken = prefs.getString(KEY_ACCESS_TOKEN, null);
        String refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null);
        return accessToken != null && !accessToken.isEmpty()
                && refreshToken != null && !refreshToken.isEmpty();
    }
}
