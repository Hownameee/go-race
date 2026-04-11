package com.grouprace.feature.profile.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.profile.R;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChangePasswordFragment extends Fragment {
    @Inject
    AppNavigator navigator;

    private ChangePasswordViewModel viewModel;

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ChangePasswordViewModel.class);
        viewModel.clearFlowState();

        ImageButton backButton = view.findViewById(R.id.change_password_back_button);
        EditText currentPasswordInput = view.findViewById(R.id.change_password_current_input);
        Button continueButton = view.findViewById(R.id.change_password_submit_button);
        Button forgotButton = view.findViewById(R.id.change_password_reset_button);

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        Runnable continueAction = () -> {
            String currentPassword = currentPasswordInput.getText().toString();
            viewModel.verifyCurrentPassword(currentPassword).observe(getViewLifecycleOwner(), result -> {
                if (result instanceof Result.Loading) {
                    continueButton.setEnabled(false);
                    continueButton.setText("Checking...");
                } else if (result instanceof Result.Success) {
                    continueButton.setEnabled(true);
                    continueButton.setText("Continue");
                    viewModel.setVerifiedCurrentPassword(currentPassword);
                    navigator.openSetNewPassword(this);
                } else if (result instanceof Result.Error) {
                    continueButton.setEnabled(true);
                    continueButton.setText("Continue");
                    Toast.makeText(requireContext(), ((Result.Error<Void>) result).message, Toast.LENGTH_SHORT).show();
                }
            });
        };

        continueButton.setOnClickListener(v -> continueAction.run());
        currentPasswordInput.setOnEditorActionListener((v, actionId, event) -> handleSubmitAction(actionId, event, continueAction));

        forgotButton.setOnClickListener(v -> requestOtpForCurrentEmail(forgotButton));
    }

    private boolean handleSubmitAction(int actionId, KeyEvent event, Runnable action) {
        boolean isDoneAction = actionId == EditorInfo.IME_ACTION_DONE;
        boolean isEnterKey = event != null
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                && event.getAction() == KeyEvent.ACTION_DOWN;

        if (isDoneAction || isEnterKey) {
            action.run();
            return true;
        }

        return false;
    }

    private void requestOtpForCurrentEmail(Button forgotButton) {
        viewModel.startProfileOtpFlow();
        viewModel.getMyInfo().observe(getViewLifecycleOwner(), profileResult -> {
            if (profileResult instanceof Result.Loading) {
                forgotButton.setEnabled(false);
                forgotButton.setText("Sending...");
            } else if (profileResult instanceof Result.Success) {
                String email = ((Result.Success<com.grouprace.core.model.Profile.MyProfileInfo>) profileResult).data.getEmail();
                viewModel.setCurrentEmail(email);
                viewModel.requestCurrentPasswordResetOtp().observe(getViewLifecycleOwner(), otpResult -> {
                    if (otpResult instanceof Result.Loading) {
                        forgotButton.setEnabled(false);
                        forgotButton.setText("Sending...");
                    } else if (otpResult instanceof Result.Success) {
                        viewModel.markOtpRequested();
                        forgotButton.setEnabled(true);
                        forgotButton.setText("Send OTP");
                        Toast.makeText(requireContext(), "OTP sent to your current email.", Toast.LENGTH_SHORT).show();
                        navigator.openPasswordResetOtp(this);
                    } else if (otpResult instanceof Result.Error) {
                        forgotButton.setEnabled(true);
                        forgotButton.setText("Send OTP");
                        Toast.makeText(requireContext(), ((Result.Error<Void>) otpResult).message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (profileResult instanceof Result.Error) {
                forgotButton.setEnabled(true);
                forgotButton.setText("Send OTP");
                Toast.makeText(requireContext(), ((Result.Error<com.grouprace.core.model.Profile.MyProfileInfo>) profileResult).message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
