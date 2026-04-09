package com.grouprace.core.network.utils; // (Bạn có thể cân nhắc chuyển sang package com.grouprace.core.data.local cho đúng chuẩn Clean Architecture)

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class SessionManager {
    private static final String PREF_NAME = "GoRaceApp";
    private static final String KEY_TOKEN = "Token";
    private static final String KEY_EXPIRE_TIME = "Token_Expire_Time";

    private static final long EXPIRATION_TIME_MS = 60 * 60 * 1000;

    private final SharedPreferences prefs;

    // 2. Thêm @Inject và @ApplicationContext cho Hilt
    @Inject
    public SessionManager(@ApplicationContext Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lưu Token và tự động tính toán thời gian hết hạn
     */
    public void saveAuthToken(String token) {
        long expireTime = System.currentTimeMillis() + EXPIRATION_TIME_MS;

        // 3. Nên gọi prefs.edit() trực tiếp khi cần lưu thay vì giữ Editor làm biến toàn cục
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putLong(KEY_EXPIRE_TIME, expireTime)
                .apply();
    }

    /**
     * Lấy Token ra. Nếu đã quá 60 phút thì tự động xóa và trả về null.
     */
    public String getAuthToken() {
        long expireTime = prefs.getLong(KEY_EXPIRE_TIME, 0);

        if (expireTime == 0) {
            return null;
        }

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
        prefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_EXPIRE_TIME)
                .apply();
        // prefs.edit().clear().apply();
    }

    /**
     * Hàm tiện ích kiểm tra nhanh xem User đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return getAuthToken() != null;
    }
}