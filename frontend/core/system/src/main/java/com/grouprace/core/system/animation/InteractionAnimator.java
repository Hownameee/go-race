package com.grouprace.core.system.animation;

import android.annotation.SuppressLint;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

public class InteractionAnimator {

    @SuppressLint("ClickableViewAccessibility")
    public static void setupSquishAnimation(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                    v.animate()
                            .scaleX(0.9f)
                            .scaleY(0.9f)
                            .setDuration(100)
                            .start();
                    return true;
                case MotionEvent.ACTION_UP:
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    v.animate()
                            .scaleX(1.1f)
                            .scaleY(1.1f)
                            .setDuration(100)
                            .withEndAction(() -> {
                                v.animate()
                                        .scaleX(1.0f)
                                        .scaleY(1.0f)
                                        .setDuration(100)
                                        .start();
                            })
                            .start();
                    v.performClick();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start();
                    return true;
            }
            return false;
        });
    }

    public static void playPopAnimation(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        view.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(150)
                .setInterpolator(new OvershootInterpolator(2.0f))
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start();
                })
                .start();
    }
}
