package com.grouprace.feature.profile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.profile.R;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileSettingsFragment extends Fragment {

    @Inject
    AppNavigator navigator;

    private ProfileSettingsViewModel viewModel;

    public static ProfileSettingsFragment newInstance() {
        return new ProfileSettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileSettingsViewModel.class);

        ImageButton backButton = view.findViewById(R.id.profile_settings_back_button);
        View changeEmailButton = view.findViewById(R.id.profile_settings_change_email_button);
        View changePasswordButton = view.findViewById(R.id.profile_settings_change_password_button);
        View contactButton = view.findViewById(R.id.profile_settings_contact_button);
        View supportButton = view.findViewById(R.id.profile_settings_support_button);
        View communityButton = view.findViewById(R.id.profile_settings_community_button);
        View legalButton = view.findViewById(R.id.profile_settings_legal_button);
        View aboutButton = view.findViewById(R.id.profile_settings_about_button);
        View logoutButton = view.findViewById(R.id.profile_settings_logout_button);
        View deleteAccountButton = view.findViewById(R.id.profile_settings_delete_account_button);

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        changeEmailButton.setOnClickListener(v -> navigateToChangeEmail());
        changePasswordButton.setOnClickListener(v -> navigateToChangePassword());
        contactButton.setOnClickListener(v -> openComingSoon("Contact"));
        supportButton.setOnClickListener(v -> openComingSoon("Support"));
        communityButton.setOnClickListener(v -> openComingSoon("Community Hub"));
        legalButton.setOnClickListener(v -> openComingSoon("Legal"));
        aboutButton.setOnClickListener(v -> openComingSoon("About"));
        logoutButton.setOnClickListener(v -> confirmLogout());
        deleteAccountButton.setOnClickListener(v -> confirmDeleteAccount());
    }

    private void navigateToChangeEmail() {
        navigator.openChangeEmailOtp(this);
    }

    private void navigateToChangePassword() {
        navigator.openChangePassword(this);
    }

    private void openComingSoon(String title) {
        navigator.openComingSoon(this, title);
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Do you want to log out of this account?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .show();
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("This action cannot be undone. Do you want to delete your account?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> deleteMyAccount())
                .show();
    }

    private void logout() {
        viewModel.logout();
        restartMainActivity();
    }

    private void deleteMyAccount() {
        viewModel.deleteMyAccount().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                viewModel.logout();
                Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                restartMainActivity();
            } else if (result instanceof Result.Error) {
                String message = ((Result.Error<Void>) result).message;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void restartMainActivity() {
        Intent intent = new Intent(requireContext(), requireActivity().getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
