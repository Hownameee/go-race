package com.grouprace.feature.posts.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
    private View editorDimScrim;
    private ImageView imgBackground;
    private ScaleGestureDetector scaleGestureDetector;
    private VisualEditorViewModel viewModel;

    private float dX, dY;

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
        editorDimScrim = view.findViewById(R.id.editor_dim_scrim);
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

        viewModel = new ViewModelProvider(this).get(VisualEditorViewModel.class);

        appNavigator.setBottomNavigationVisibility(this, false);

        initData(view);
        setupGestures();
        setupButtons(view);
        setupObservers();
    }

    private void setupObservers() {
        viewModel.getTextColor().observe(getViewLifecycleOwner(), this::applyTextColor);
        viewModel.getBgColor().observe(getViewLifecycleOwner(), this::applyOverlayBackground);
        viewModel.getScaleFactor().observe(getViewLifecycleOwner(), scale -> {
            statsOverlay.setScaleX(scale);
            statsOverlay.setScaleY(scale);
        });
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
                Float currentScale = viewModel.getScaleFactor().getValue();
                float newScale = (currentScale != null ? currentScale : 1.0f) * detector.getScaleFactor();
                newScale = Math.max(0.5f, Math.min(newScale, 3.0f));
                viewModel.setScaleFactor(newScale);
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
            viewModel.reset();

            statsOverlay.animate()
                    .translationX(0)
                    .translationY(0)
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(500)
                    .start();
        });

        view.findViewById(R.id.btn_share_visual).setOnClickListener(v -> shareCapturedBitmap(view));
    }

    private void showColorPicker() {
        Integer textColor = viewModel.getTextColor().getValue();
        Integer bgColor = viewModel.getBgColor().getValue();
        
        ColorPickerBottomSheet bottomSheet = ColorPickerBottomSheet.newInstance(
            textColor != null ? textColor : Color.WHITE,
            bgColor != null ? bgColor : 0x80000000
        );
        
        bottomSheet.setOnColorChangeListener(viewModel::setTextColor);
        bottomSheet.setOnBackgroundChangeListener(viewModel::setBgColor);
        
        bottomSheet.show(getChildFragmentManager(), "ColorPickerBottomSheet");
        
        statsOverlay.post(() -> {
            View sheetView = bottomSheet.getView();
            View rootView = getView();
            if (sheetView != null && rootView != null) {
                float density = getResources().getDisplayMetrics().density;
                int sheetHeight = sheetView.getHeight();
                if (sheetHeight == 0) {
                    sheetHeight = (int) (360 * density);
                }
                
                Float currentScale = viewModel.getScaleFactor().getValue();
                viewModel.savePreviewPosition(
                    statsOverlay.getX(),
                    statsOverlay.getY(),
                    currentScale != null ? currentScale : 1.0f
                );

                float targetX = (rootView.getWidth() - statsOverlay.getWidth()) / 2f;
                float targetY = rootView.getHeight() - sheetHeight - (120 * density) - statsOverlay.getHeight();

                statsOverlay.animate()
                        .x(targetX)
                        .y(targetY)
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(500)
                        .start();
                
                viewModel.setScaleFactor(1.0f);
                
                editorDimScrim.setVisibility(View.VISIBLE);
                editorDimScrim.animate().alpha(1f).setDuration(500).start();
            }
        });

        bottomSheet.setOnDismissListener(() -> {
            statsOverlay.animate()
                    .x(viewModel.getSavedX())
                    .y(viewModel.getSavedY())
                    .scaleX(viewModel.getSavedScale())
                    .scaleY(viewModel.getSavedScale())
                    .setDuration(300)
                    .start();
            
            viewModel.setScaleFactor(viewModel.getSavedScale());
            
            editorDimScrim.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> editorDimScrim.setVisibility(View.GONE))
                    .start();
        });
    }

    private void applyTextColor(int color) {
        for (TextView tv : textViewsToColor) {
            tv.setTextColor(color);
        }
        for (ImageView iv : imageViewsToColor) {
            iv.setColorFilter(color);
        }
    }

    private void applyOverlayBackground(int color) {
        float density = getResources().getDisplayMetrics().density;
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(16 * density);
        statsOverlay.setBackground(gd);
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

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            sendIntent.setType("image/png");
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(sendIntent, "Share your dynamic card"));

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to share", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
