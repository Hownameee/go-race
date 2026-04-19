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
                            Bitmap card = ShareCardGenerator.generate(
                                    resource,
                                    getResources().getDrawable(com.grouprace.core.system.R.drawable.ic_run, null),
                                    getResources().getDrawable(com.grouprace.core.system.R.drawable.ic_app, null),
                                    title, distance, duration, speed
                            );
                            shareImage(card, title, distance, pace, duration, username);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {}

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            Bitmap card = ShareCardGenerator.generate(
                                    null,
                                    getResources().getDrawable(com.grouprace.core.system.R.drawable.ic_run, null),
                                    getResources().getDrawable(com.grouprace.core.system.R.drawable.ic_app, null),
                                    title, distance, duration, speed
                            );
                            shareImage(card, title, distance, pace, duration, username);
                        }
                    });
        } else {
            Bitmap card = ShareCardGenerator.generate(
                    null,
                    getResources().getDrawable(com.grouprace.core.system.R.drawable.ic_run, null),
                    getResources().getDrawable(com.grouprace.core.system.R.drawable.ic_app, null),
                    title, distance, duration, speed
            );
            shareImage(card, title, distance, pace, duration, username);
        }
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
