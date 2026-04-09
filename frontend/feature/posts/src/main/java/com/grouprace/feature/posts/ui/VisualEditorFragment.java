package com.grouprace.feature.posts.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.posts.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class VisualEditorFragment extends Fragment {

    @Inject
    AppNavigator appNavigator;

    private static final String ARG_PHOTO_URI = "arg_photo_uri";
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_DISTANCE = "arg_distance";
    private static final String ARG_TIME = "arg_time";
    private static final String ARG_SPEED = "arg_speed";

    private View statsOverlay;
    private ImageView imgBackground;
    private ScaleGestureDetector scaleGestureDetector;

    private float scaleFactor = 1.0f;
    private float dX, dY;
    private int selectedColor = Color.WHITE;

    private List<TextView> textViewsToColor = new ArrayList<>();
    private List<ImageView> imageViewsToColor = new ArrayList<>();

    public static VisualEditorFragment newInstance(String photoUri, String title, String distance, String time, String speed) {
        VisualEditorFragment fragment = new VisualEditorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_URI, photoUri);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DISTANCE, distance);
        args.putString(ARG_TIME, time);
        args.putString(ARG_SPEED, speed);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visual_editor, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statsOverlay = view.findViewById(R.id.stats_overlay);
        imgBackground = view.findViewById(R.id.img_background);

        // Populate views for coloring
        textViewsToColor.add(view.findViewById(R.id.tv_activity_title));
        textViewsToColor.add(view.findViewById(R.id.tv_stat_distance));
        textViewsToColor.add(view.findViewById(R.id.tv_stat_time));
        textViewsToColor.add(view.findViewById(R.id.tv_stat_speed));
        
        // Add labels to color list
        textViewsToColor.add(view.findViewById(R.id.tv_label_distance));
        textViewsToColor.add(view.findViewById(R.id.tv_label_time));
        textViewsToColor.add(view.findViewById(R.id.tv_label_speed));
        textViewsToColor.add(view.findViewById(R.id.tv_branding_name));
        
        imageViewsToColor.add(view.findViewById(R.id.ic_activity_type));
        imageViewsToColor.add(view.findViewById(R.id.ic_logo));

        appNavigator.setBottomNavigationVisibility(this, false);

        initData(view);
        setupGestures();
        setupButtons(view);
    }

    @Override
    public void onDestroyView() {
        appNavigator.setBottomNavigationVisibility(this, true);
        super.onDestroyView();
    }

    private void initData(View view) {
        Bundle args = getArguments();
        if (args == null) return;

        String photoUriStr = args.getString(ARG_PHOTO_URI);
        if (photoUriStr != null) {
            Glide.with(this).load(Uri.parse(photoUriStr)).into(imgBackground);
        }

        ((TextView) view.findViewById(R.id.tv_activity_title)).setText(args.getString(ARG_TITLE));
        ((TextView) view.findViewById(R.id.tv_stat_distance)).setText(args.getString(ARG_DISTANCE));
        ((TextView) view.findViewById(R.id.tv_stat_time)).setText(args.getString(ARG_TIME));
        ((TextView) view.findViewById(R.id.tv_stat_speed)).setText(args.getString(ARG_SPEED));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupGestures() {
        scaleGestureDetector = new ScaleGestureDetector(requireContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 3.0f));
                statsOverlay.setScaleX(scaleFactor);
                statsOverlay.setScaleY(scaleFactor);
                return true;
            }
        });

        statsOverlay.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!scaleGestureDetector.isInProgress()) {
                        v.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                    }
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

    private void setupButtons(View view) {
        view.findViewById(R.id.btn_close_editor).setOnClickListener(v -> {
            if (isAdded()) {
                getParentFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.btn_color_picker).setOnClickListener(v -> showColorPicker());
        
        view.findViewById(R.id.btn_reset).setOnClickListener(v -> {
            scaleFactor = 1.0f;
            statsOverlay.setScaleX(1.0f);
            statsOverlay.setScaleY(1.0f);
            statsOverlay.setTranslationX(0);
            statsOverlay.setTranslationY(0);
        });

        view.findViewById(R.id.btn_share_visual).setOnClickListener(v -> shareCapturedBitmap(view));
    }

    private void showColorPicker() {
        final int[] colors = {Color.WHITE, Color.BLACK, Color.YELLOW, Color.CYAN, Color.RED, Color.GREEN, Color.MAGENTA, Color.BLUE};
        final String[] colorNames = {"White", "Black", "Yellow", "Cyan", "Red", "Green", "Magenta", "Blue"};

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Pick Text Color")
                .setItems(colorNames, (dialog, which) -> {
                    updateTextColor(colors[which]);
                })
                .show();
    }

    private void updateTextColor(int color) {
        selectedColor = color;
        for (TextView tv : textViewsToColor) {
            tv.setTextColor(color);
        }
        for (ImageView iv : imageViewsToColor) {
            iv.setColorFilter(color);
        }
    }

    private void shareCapturedBitmap(View view) {
        View surface = view.findViewById(R.id.editor_surface);
        Bitmap bitmap = Bitmap.createBitmap(surface.getWidth(), surface.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        surface.draw(canvas);

        try {
            File cachePath = new File(requireContext().getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "visual_share.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    file
            );

            android.content.Intent sendIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            sendIntent.putExtra(android.content.Intent.EXTRA_STREAM, contentUri);
            sendIntent.setType("image/png");
            sendIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(android.content.Intent.createChooser(sendIntent, "Share your dynamic card"));

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to share", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
