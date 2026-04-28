package com.grouprace.feature.profile.ui.edit;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.MyProfileInfo;
import com.grouprace.core.system.ui.DatePickerHelper;
import com.grouprace.feature.profile.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileFragment extends Fragment {

    private EditProfileViewModel viewModel;

    private ImageButton backButton;
    private Button saveButton;
    private Button changeAvatarButton;
    private ImageView avatarImageView;

    private EditText editFullName;
    private EditText editUsername;
    private EditText editBirthdate;
    private EditText editBio;
    private EditText editProvinceCity;
    private EditText editCountry;
    private EditText editHeight;
    private EditText editWeight;
    private Uri selectedAvatarUri;

    private final ActivityResultLauncher<String> avatarPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleAvatarSelected);

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        initViews(view);
        setupListeners();
        observeViewModel();
        loadMyInfo();
    }

    private void initViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        saveButton = view.findViewById(R.id.button_save_profile);
        changeAvatarButton = view.findViewById(R.id.button_change_avatar);
        avatarImageView = view.findViewById(R.id.edit_profile_avatar_image);

        editFullName = view.findViewById(R.id.edit_profile_full_name);
        editUsername = view.findViewById(R.id.edit_profile_username);
        editBirthdate = view.findViewById(R.id.edit_profile_birthdate);
        editBio = view.findViewById(R.id.edit_profile_bio);
        editProvinceCity = view.findViewById(R.id.edit_profile_province_city);
        editCountry = view.findViewById(R.id.edit_profile_country);
        editHeight = view.findViewById(R.id.edit_profile_height);
        editWeight = view.findViewById(R.id.edit_profile_weight);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        saveButton.setOnClickListener(v -> saveMyInfo());
        changeAvatarButton.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));
        DatePickerHelper.attachDatePicker(this, editBirthdate);
        editWeight.setOnEditorActionListener((v, actionId, event) -> {
            boolean isDoneAction = actionId == EditorInfo.IME_ACTION_DONE;
            boolean isEnterKey = event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN;

            if (isDoneAction || isEnterKey) {
                saveMyInfo();
                return true;
            }
            return false;
        });
    }

    private void observeViewModel() {
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMyInfo() {
        viewModel.getMyInfo().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                saveButton.setEnabled(false);
                saveButton.setText("Loading...");
            } else if (result instanceof Result.Success) {
                saveButton.setEnabled(true);
                saveButton.setText("Save");
                bindMyInfo(((Result.Success<MyProfileInfo>) result).data);
            } else if (result instanceof Result.Error) {
                saveButton.setEnabled(true);
                saveButton.setText("Save");
                String errorMsg = ((Result.Error<MyProfileInfo>) result).message;
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveMyInfo() {
        viewModel.updateMyInfo(
                editUsername.getText().toString().trim(),
                editFullName.getText().toString().trim(),
                editBirthdate.getText().toString().trim(),
                editBio.getText().toString().trim(),
                editProvinceCity.getText().toString().trim(),
                editCountry.getText().toString().trim(),
                editHeight.getText().toString().trim(),
                editWeight.getText().toString().trim()
        ).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                saveButton.setEnabled(false);
                saveButton.setText("Saving...");
            } else if (result instanceof Result.Success) {
                saveButton.setEnabled(true);
                saveButton.setText("Save");
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            } else if (result instanceof Result.Error) {
                saveButton.setEnabled(true);
                saveButton.setText("Save");
                String errorMsg = ((Result.Error<Void>) result).message;
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAvatarSelected(@Nullable Uri uri) {
        if (uri == null || !isAdded()) {
            return;
        }

        selectedAvatarUri = uri;
        avatarImageView.setImageURI(uri);
        uploadAvatar(uri);
    }

    private void uploadAvatar(@NonNull Uri uri) {
        byte[] avatarBytes;
        try {
            avatarBytes = readBytesFromUri(uri);
        } catch (IOException exception) {
            Toast.makeText(requireContext(), "Unable to read avatar image.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = getFileName(uri);
        String mimeType = requireContext().getContentResolver().getType(uri);

        viewModel.uploadMyAvatar(avatarBytes, fileName, mimeType)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Loading) {
                        changeAvatarButton.setEnabled(false);
                        changeAvatarButton.setText("Uploading...");
                    } else if (result instanceof Result.Success) {
                        changeAvatarButton.setEnabled(true);
                        changeAvatarButton.setText("Change Avatar");
                        String avatarUrl = ((Result.Success<String>) result).data;
                        viewModel.setCurrentAvatarUrl(avatarUrl);
                        Toast.makeText(requireContext(), "Avatar uploaded successfully!", Toast.LENGTH_SHORT).show();
                    } else if (result instanceof Result.Error) {
                        changeAvatarButton.setEnabled(true);
                        changeAvatarButton.setText("Change Avatar");
                        String errorMsg = ((Result.Error<String>) result).message;
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void bindMyInfo(MyProfileInfo profileInfo) {
        if (profileInfo == null) {
            return;
        }

        viewModel.setCurrentAvatarUrl(profileInfo.getAvatarUrl());
        bindEditText(editFullName, profileInfo.getFullname());
        bindEditText(editUsername, profileInfo.getUsername());
        bindEditText(editBirthdate, profileInfo.getBirthdate());
        bindEditText(editBio, profileInfo.getBio());
        bindEditText(editProvinceCity, profileInfo.getProvinceCity());
        bindEditText(editCountry, profileInfo.getCountry());
        bindEditText(editHeight, profileInfo.getHeightCm() != null ? String.valueOf(profileInfo.getHeightCm()) : null);
        bindEditText(editWeight, profileInfo.getWeightKg() != null ? String.valueOf(profileInfo.getWeightKg()) : null);
        loadAvatarPreview(profileInfo.getAvatarUrl());
    }

    private void bindEditText(EditText editText, String value) {
        editText.setText(value != null ? value : "");
    }

    private void loadAvatarPreview(@Nullable String avatarUrl) {
        if (avatarUrl == null || avatarUrl.trim().isEmpty() || selectedAvatarUri != null) {
            return;
        }

        new Thread(() -> {
            try (InputStream inputStream = new URL(avatarUrl).openStream()) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null && isAdded()) {
                    requireActivity().runOnUiThread(() -> avatarImageView.setImageBitmap(bitmap));
                }
            } catch (IOException ignored) {
            }
        }).start();
    }

    private byte[] readBytesFromUri(@NonNull Uri uri) throws IOException {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (inputStream == null) {
                throw new IOException("Input stream is null");
            }

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        }
    }

    private String getFileName(@NonNull Uri uri) {
        Cursor cursor = requireContext().getContentResolver()
                .query(uri, null, null, null, null);

        if (cursor != null) {
            try {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    return cursor.getString(nameIndex);
                }
            } finally {
                cursor.close();
            }
        }

        return "avatar-" + System.currentTimeMillis() + ".jpg";
    }
}
