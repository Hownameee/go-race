package com.grouprace.core.network.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final String PREF_NAME = "GoRaceApp";
    private final String KEY_TOKEN = "Token";
    private final String KEY_EXPIRE_TIME = "Token_Expire_Time";

    private static final long EXPIRATION_TIME_MS = 60 * 60 * 1000;

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Lưu Token và tự động tính toán thời gian hết hạn
     */
    public void saveAuthToken(String token) {
        long expireTime = System.currentTimeMillis() + EXPIRATION_TIME_MS;

        editor.putString(KEY_TOKEN, token);
        editor.putLong(KEY_EXPIRE_TIME, expireTime);
        editor.apply();
    }

    /**
     * Lấy Token ra. Nếu đã quá 60 phút thì tự động xóa và trả về null.
     */
    public String getAuthToken() {
        long expireTime = prefs.getLong(KEY_EXPIRE_TIME, 0);
        long currentTime = System.currentTimeMillis();

        if (currentTime > expireTime) {
            clearSession();
            return null;
        }

        return prefs.getString(KEY_TOKEN, null);
    }

    /**
     * Xóa toàn bộ dữ liệu (Dùng khi Đăng xuất hoặc Token hết hạn)
     */
    public void clearSession() {
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_EXPIRE_TIME);
        editor.apply();
    }

    /**
     * Hàm tiện ích kiểm tra nhanh xem User đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return getAuthToken() != null;
    }
}
