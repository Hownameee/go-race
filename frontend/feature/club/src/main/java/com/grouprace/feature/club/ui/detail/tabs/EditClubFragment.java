package com.grouprace.feature.club.ui.detail.tabs;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.club.R;

import javax.inject.Inject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditClubFragment extends Fragment {

    private static final String ARG_CLUB_ID = "CLUB_ID";

    @Inject
    AppNavigator appNavigator;

    private EditClubViewModel viewModel;
    private int clubId;
    private Uri selectedImageUri = null;
    private ImageView ivEditAvatar;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this).load(uri).circleCrop().into(ivEditAvatar);
                }
            });

    public static EditClubFragment newInstance(int clubId) {
        EditClubFragment fragment = new EditClubFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLUB_ID, clubId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_club, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clubId = getArguments() != null ? getArguments().getInt(ARG_CLUB_ID, -1) : -1;
        if (clubId == -1) return;

        viewModel = new ViewModelProvider(this).get(EditClubViewModel.class);

        TopAppBarHelper.setupTopAppBar(view, new TopAppBarConfig.Builder()
                .setTitle("Edit Club")
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back)
                .setOnLeftIconClick(v -> requireActivity().getSupportFragmentManager().popBackStack())
                .build());

        ivEditAvatar = view.findViewById(R.id.iv_edit_avatar);
        EditText etName = view.findViewById(R.id.et_edit_name);
        EditText etDescription = view.findViewById(R.id.et_edit_description);
        View btnChangeImage = view.findViewById(R.id.btn_change_image);
        Button btnSave = view.findViewById(R.id.btn_save_club);

        // Pre-fill current club info
        viewModel.getClub(clubId).observe(getViewLifecycleOwner(), club -> {
            if (club != null) {
                if (etName.getText().toString().isEmpty()) {
                    etName.setText(club.getName());
                }
                if (etDescription.getText().toString().isEmpty()) {
                    etDescription.setText(club.getDescription());
                }
                if (club.getAvatarUrl() != null && !club.getAvatarUrl().isEmpty() && selectedImageUri == null) {
                    Glide.with(this).load(club.getAvatarUrl()).circleCrop().into(ivEditAvatar);
                }
            }
        });

        btnChangeImage.setOnClickListener(v ->
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build())
        );
        btnSave.setOnClickListener(v -> saveClub(etName, etDescription, btnSave));
    }

    private void saveClub(EditText etName, EditText etDescription, Button btnSave) {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        byte[] imageBytes = null;
        String mimeType = null;

        if (selectedImageUri != null) {
            mimeType = requireContext().getContentResolver().getType(selectedImageUri);

            if (!"image/png".equals(mimeType) && !"image/jpeg".equals(mimeType)) {
                Toast.makeText(getContext(), "Only PNG and JPEG images are allowed.", Toast.LENGTH_SHORT).show();
                return;
            }

            imageBytes = readImageBytes(selectedImageUri);
            if (imageBytes == null) {
                Toast.makeText(getContext(), "Failed to read image.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        btnSave.setEnabled(false);

        final byte[] finalImageBytes = imageBytes;
        final String finalMimeType = mimeType;

        viewModel.updateClub(
                clubId,
                name.isEmpty() ? null : name,
                description.isEmpty() ? null : description,
                finalImageBytes,
                finalMimeType
        ).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) return;
            btnSave.setEnabled(true);
            if (result instanceof Result.Success) {
                Toast.makeText(getContext(), "Club updated!", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else if (result instanceof Result.Error) {
                String msg = ((Result.Error<?>) result).message;
                Toast.makeText(getContext(), "Failed: " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private byte[] readImageBytes(Uri uri) {
        try (InputStream is = requireContext().getContentResolver().openInputStream(uri);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            if (is == null) return null;
            byte[] buf = new byte[4096];
            int len;
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}
