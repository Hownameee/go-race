package com.grouprace.feature.club.ui;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.grouprace.feature.club.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

public class ShareClubFragment extends BottomSheetDialogFragment {

    private static final String ARG_CLUB_NAME = "club_name";
    private static final String ARG_MEMBER_COUNT = "member_count";
    private static final String ARG_AVATAR_URL = "avatar_url";

    public static ShareClubFragment newInstance(String name, int memberCount, String avatarUrl) {
        ShareClubFragment fragment = new ShareClubFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CLUB_NAME, name);
        args.putInt(ARG_MEMBER_COUNT, memberCount);
        args.putString(ARG_AVATAR_URL, avatarUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_club, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.card_share_club).setOnClickListener(v -> generateAndShare());
    }

    private void generateAndShare() {
        Bundle args = getArguments();
        if (args == null) return;

        String name = args.getString(ARG_CLUB_NAME, "Club");
        int count = args.getInt(ARG_MEMBER_COUNT, 0);
        String avatarUrl = args.getString(ARG_AVATAR_URL, null);

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this).asBitmap().load(avatarUrl).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Bitmap card = generateClubCard(resource, name, count);
                    shareImage(card, name);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    Bitmap card = generateClubCard(null, name, count);
                    shareImage(card, name);
                }
            });
        } else {
            Bitmap card = generateClubCard(null, name, count);
            shareImage(card, name);
        }
    }

    private Bitmap generateClubCard(@Nullable Bitmap avatar, String name, int memberCount) {
        int width = 1080;
        int height = 1920;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 1. Background Gradient (Sleek Dark Mode)
        Paint bgPaint = new Paint();
        LinearGradient bgGradient = new LinearGradient(0, 0, 0, height, Color.parseColor("#1A1A1A"), Color.BLACK, Shader.TileMode.CLAMP);
        bgPaint.setShader(bgGradient);
        canvas.drawRect(0, 0, width, height, bgPaint);

        // 2. Avatar (Center)
        int avatarSize = 400;
        int centerX = width / 2;
        int centerY = height / 2 - 200;

        Paint avatarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (avatar != null) {
            Bitmap scaledAvatar = Bitmap.createScaledBitmap(avatar, avatarSize, avatarSize, true);

            // Draw circle mask
            canvas.save();
            android.graphics.Path path = new android.graphics.Path();
            path.addCircle(centerX, centerY, avatarSize / 2f, android.graphics.Path.Direction.CCW);
            canvas.clipPath(path);
            canvas.drawBitmap(scaledAvatar, centerX - avatarSize / 2f, centerY - avatarSize / 2f, avatarPaint);
            canvas.restore();
            scaledAvatar.recycle();
        } else {
            avatarPaint.setColor(Color.parseColor("#333333"));
            canvas.drawCircle(centerX, centerY, avatarSize / 2f, avatarPaint);
        }

        // 3. Club Name
        Paint namePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        namePaint.setColor(Color.WHITE);
        namePaint.setTextSize(80);
        namePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        namePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(name, centerX, centerY + avatarSize / 2f + 120, namePaint);

        // 4. Member Count
        Paint statsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        statsPaint.setColor(Color.parseColor("#00BFA5")); // Teak/Accent color
        statsPaint.setTextSize(48);
        statsPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        statsPaint.setTextAlign(Paint.Align.CENTER);
        String stats = String.format(Locale.getDefault(), "%d MEMBERS", memberCount);
        canvas.drawText(stats, centerX, centerY + avatarSize / 2f + 200, statsPaint);

        // 5. GoRace Branding (Bottom)
        Paint brandingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        brandingPaint.setColor(Color.WHITE);
        brandingPaint.setTextSize(40);
        brandingPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        brandingPaint.setTextAlign(Paint.Align.CENTER);

        Drawable logo = getResources().getDrawable(com.grouprace.core.system.R.drawable.ic_app, null);
        if (logo != null) {
            int logoSize = 60;
            logo.setBounds(centerX - logoSize / 2, height - 200, centerX + logoSize / 2, height - 200 + logoSize);
            logo.setTint(Color.WHITE);
            logo.draw(canvas);
        }
        canvas.drawText("GORACE", centerX, height - 100, brandingPaint);

        return bitmap;
    }

    private void shareImage(Bitmap bitmap, String clubName) {
        try {
            File cachePath = new File(requireContext().getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "share_club.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", file);

            String shareText = "Join our club '" + clubName + "' on GoRace!";

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            sendIntent.setType("image/png");
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent shareIntent = Intent.createChooser(sendIntent, "Invite to Club");
            startActivity(shareIntent);
            dismiss();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to share image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
