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
import android.widget.TextView;
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
public class ChangeEmailOtpFragment extends Fragment {
    @Inject
    AppNavigator navigator;

    private ChangeEmailViewModel viewModel;

    public static ChangeEmailOtpFragment newInstance() {
        return new ChangeEmailOtpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_email_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ChangeEmailViewModel.class);
        viewModel.resetFlow();

        ImageButton backButton = view.findViewById(R.id.change_email_otp_back_button);
        TextView messageView = view.findViewById(R.id.change_email_otp_message);
        EditText otpInput = view.findViewById(R.id.change_email_otp_input);
        Button submitButton = view.findViewById(R.id.change_email_confirm_button);

        messageView.setText("Enter the OTP sent to your current email before choosing a new email.");

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        if (!viewModel.isOtpRequested()) {
            requestOtp(messageView, submitButton);
        }

        Runnable submitOtp = () -> viewModel.verifyOtp(otpInput.getText().toString())
                .observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Loading) {
                        submitButton.setEnabled(false);
                        submitButton.setText("Verifying...");
                    } else if (result instanceof Result.Success) {
                        viewModel.markCurrentEmailVerified();
                        submitButton.setEnabled(true);
                        submitButton.setText("Verify OTP");
                        Toast.makeText(requireContext(), "OTP verified successfully", Toast.LENGTH_SHORT).show();
                        navigator.openChangeEmail(this);
                    } else if (result instanceof Result.Error) {
                        submitButton.setEnabled(true);
                        submitButton.setText("Verify OTP");
                        Toast.makeText(requireContext(), ((Result.Error<Void>) result).message, Toast.LENGTH_SHORT).show();
                    }
                });

        submitButton.setOnClickListener(v -> submitOtp.run());
        otpInput.setOnEditorActionListener((v, actionId, event) -> handleSubmitAction(actionId, event, submitOtp));
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

    private void requestOtp(TextView messageView, Button submitButton) {
        viewModel.requestOtp().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                submitButton.setEnabled(false);
                messageView.setText("Sending OTP to your current email...");
            } else if (result instanceof Result.Success) {
                viewModel.markOtpRequested();
                submitButton.setEnabled(true);
                messageView.setText("Enter the OTP sent to your current email before choosing a new email.");
                Toast.makeText(requireContext(), "OTP sent to your current email.", Toast.LENGTH_SHORT).show();
            } else if (result instanceof Result.Error) {
                submitButton.setEnabled(true);
                messageView.setText("Unable to send OTP. Please go back and try again.");
                Toast.makeText(requireContext(), ((Result.Error<Void>) result).message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
