package com.grouprace.feature.posts.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

public class ShareCardGenerator {

    private static final int COLOR_WHITE = Color.WHITE;
    private static final int COLOR_BLACK = Color.BLACK;

    public static Bitmap generate(
            @Nullable Bitmap backgroundImage,
            @Nullable Drawable activityIcon,
            @Nullable Drawable logoIcon,
            String title,
            String distance,
            String duration,
            String speed
    ) {
        int cardWidth = 1080;
        int cardHeight = 1920;

        Bitmap bitmap = Bitmap.createBitmap(cardWidth, cardHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if (backgroundImage != null) {
            float scaleX = (float) cardWidth / backgroundImage.getWidth();
            float scaleY = (float) cardHeight / backgroundImage.getHeight();
            float scale = Math.max(scaleX, scaleY);
            int scaledWidth = (int) (backgroundImage.getWidth() * scale);
            int scaledHeight = (int) (backgroundImage.getHeight() * scale);
            int left = (cardWidth - scaledWidth) / 2;
            int top = (cardHeight - scaledHeight) / 2;
            Bitmap scaledBg = Bitmap.createScaledBitmap(backgroundImage, scaledWidth, scaledHeight, true);
            canvas.drawBitmap(scaledBg, left, top, null);
            scaledBg.recycle();
        } else {
            canvas.drawColor(COLOR_BLACK);
        }

        Paint gradientPaint = new Paint();
        LinearGradient gradient = new LinearGradient(
                0, cardHeight * 0.35f, 0, cardHeight,
                Color.TRANSPARENT, COLOR_BLACK,
                Shader.TileMode.CLAMP
        );
        gradientPaint.setShader(gradient);
        canvas.drawRect(0, cardHeight * 0.35f, cardWidth, cardHeight, gradientPaint);

        if (activityIcon != null) {
            int iconSize = 80;
            int iconLeft = 72;
            int iconTop = cardHeight - 680;
            activityIcon.setBounds(iconLeft, iconTop, iconLeft + iconSize, iconTop + iconSize);
            activityIcon.setTint(COLOR_WHITE);
            activityIcon.draw(canvas);
        }

        if (logoIcon != null) {
            int logoSize = 60;
            int logoRight = cardWidth - 72;
            int logoTop = cardHeight - 670;
            logoIcon.setBounds(logoRight - logoSize, logoTop, logoRight, logoTop + logoSize);
            logoIcon.setTint(COLOR_WHITE);
            logoIcon.draw(canvas);
        }
        
        Paint logoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        logoPaint.setColor(COLOR_WHITE);
        logoPaint.setTextSize(48);
        logoPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        logoPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("GORACE", cardWidth - 72 - 70, cardHeight - 625, logoPaint);

        Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(COLOR_WHITE);
        titlePaint.setTextSize(72);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(title, 72, cardHeight - 520, titlePaint);

        Paint dividerPaint = new Paint();
        dividerPaint.setColor(COLOR_WHITE);
        dividerPaint.setStrokeWidth(2);
        canvas.drawLine(72, cardHeight - 480, cardWidth - 72, cardHeight - 480, dividerPaint);

        Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(COLOR_WHITE);
        labelPaint.setTextSize(36);
        labelPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        int colWidth = (cardWidth - 144) / 3;
        int startX = 72;
        int labelY = cardHeight - 420;
        int valueY = cardHeight - 350;

        canvas.drawText("Distance", startX, labelY, labelPaint);
        canvas.drawText("Time", startX + colWidth, labelY, labelPaint);
        canvas.drawText("Speed", startX + colWidth * 2, labelY, labelPaint);

        Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setColor(COLOR_WHITE);
        valuePaint.setTextSize(64);
        valuePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText(distance, startX, valueY, valuePaint);
        canvas.drawText(duration, startX + colWidth, valueY, valuePaint);
        canvas.drawText(speed, startX + colWidth * 2, valueY, valuePaint);

        return bitmap;
    }
}
