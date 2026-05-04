package com.grouprace.feature.profile.ui.main.components;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class ProfileAvatarLoader {
    private ProfileAvatarLoader() {
    }

    public static void load(Fragment fragment, ImageView target, @Nullable String avatarUrl) {
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            target.setImageDrawable(null);
            return;
        }

        new Thread(() -> {
            try (InputStream inputStream = new URL(avatarUrl).openStream()) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null && fragment.isAdded()) {
                    fragment.requireActivity().runOnUiThread(() -> target.setImageBitmap(bitmap));
                }
            } catch (IOException ignored) {
            }
        }).start();
    }
}
