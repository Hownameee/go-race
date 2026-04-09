package com.grouprace.feature.posts.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.posts.R;

import java.io.File;
import java.io.FileOutputStream;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShareActivityFragment extends BottomSheetDialogFragment {

    @Inject
    protected AppNavigator appNavigator;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    private static final String ARG_TITLE = "title";
    private static final String ARG_DISTANCE = "distance";
    private static final String ARG_PACE = "pace";
    private static final String ARG_DURATION = "duration";
    private static final String ARG_USERNAME = "username";
    private static final String ARG_RECORD_IMAGE_URL = "record_image_url";
    private static final String ARG_SPEED = "speed";

    private static final int COLOR_WHITE = Color.WHITE;
    private static final int COLOR_BLACK = Color.BLACK;

    public static ShareActivityFragment newInstance(String title, String distance, String pace, String duration, String username, String recordImageUrl, String speed) {
        ShareActivityFragment fragment = new ShareActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DISTANCE, distance);
        args.putString(ARG_PACE, pace);
        args.putString(ARG_DURATION, duration);
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_RECORD_IMAGE_URL, recordImageUrl);
        args.putString(ARG_SPEED, speed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Bundle args = getArguments();
                if (args == null) return;
                
                String photoUri = uri.toString();
                String title = args.getString(ARG_TITLE, "Activity");
                String distance = args.getString(ARG_DISTANCE, "--");
                String time = args.getString(ARG_DURATION, "--");
                String speed = args.getString(ARG_SPEED, "--");

                Fragment hostFragment = getParentFragment();
                if (hostFragment == null) {
                    hostFragment = this;
                }


                appNavigator.navigateToVisualEditor(
                        hostFragment,
                        photoUri,
                        title,
                        distance,
                        time,
                        speed
                );
                dismiss();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout cardBasic = view.findViewById(R.id.card_template_basic);
        LinearLayout cardVisual = view.findViewById(R.id.card_template_visual);

        cardBasic.setOnClickListener(v -> shareBasicTemplate());

        cardVisual.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }

    private void shareBasicTemplate() {
        Bundle args = getArguments();
        if (args == null) return;

        String title = args.getString(ARG_TITLE, "Activity");
        String distance = args.getString(ARG_DISTANCE, "--");
        String pace = args.getString(ARG_PACE, "--");
        String duration = args.getString(ARG_DURATION, "--");
        String username = args.getString(ARG_USERNAME, "");
        String recordImageUrl = args.getString(ARG_RECORD_IMAGE_URL, null);
        String speed = args.getString(ARG_SPEED, "--");

        if (recordImageUrl != null && !recordImageUrl.isEmpty()) {
            Glide.with(requireContext())
                    .asBitmap()
                    .load(recordImageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Bitmap card = generateShareCard(resource, title, distance, duration, speed);
                            shareImage(card, title, distance, pace, duration, username);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {}

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            Bitmap card = generateShareCard(null, title, distance, duration, speed);
                            shareImage(card, title, distance, pace, duration, username);
                        }
                    });
        } else {
            Bitmap card = generateShareCard(null, title, distance, duration, speed);
            shareImage(card, title, distance, pace, duration, username);
        }
    }

    private Bitmap generateShareCard(@Nullable Bitmap backgroundImage, String title, String distance, String duration, String speed) {
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

        Drawable activityIcon = getResources().getDrawable(com.grouprace.core.system.R.drawable.ic_run, null);
        if (activityIcon != null) {
            int iconSize = 80;
            int iconLeft = 72;
            int iconTop = cardHeight - 680;
            activityIcon.setBounds(iconLeft, iconTop, iconLeft + iconSize, iconTop + iconSize);
            activityIcon.setTint(COLOR_WHITE);
            activityIcon.draw(canvas);
        }

        Drawable logoIcon = getResources().getDrawable(com.grouprace.core.system.R.drawable.ic_app, null);
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

    private void shareImage(Bitmap bitmap, String title, String distance, String pace, String duration, String username) {
        try {
            File cachePath = new File(requireContext().getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "share_activity.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    file
            );

            String shareText = username + " on GoRace";

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            sendIntent.setType("image/png");
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent shareIntent = Intent.createChooser(sendIntent, "Share your activity");
            startActivity(shareIntent);
            dismiss();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to share image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
