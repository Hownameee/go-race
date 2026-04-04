package com.grouprace.feature.profile.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.grouprace.feature.profile.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PasswordResetOtpFragment extends Fragment {
    public interface NavigationHost {
        void openSetNewPassword();
    }

    private ChangePasswordViewModel viewModel;

    public static PasswordResetOtpFragment newInstance() {
        return new PasswordResetOtpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password_reset_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ChangePasswordViewModel.class);

        ImageButton backButton = view.findViewById(R.id.password_reset_otp_back_button);
        TextView messageView = view.findViewById(R.id.password_reset_otp_message);
        EditText otpInput = view.findViewById(R.id.password_reset_otp_input);
        Button submitButton = view.findViewById(R.id.password_reset_otp_submit_button);

        if (viewModel.getResetEmail() != null) {
            messageView.setText("Enter the OTP sent to " + viewModel.getResetEmail() + ".");
        }

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        submitButton.setOnClickListener(v -> {
            String otpCode = otpInput.getText().toString();
            viewModel.verifyResetOtp(otpCode).observe(getViewLifecycleOwner(), result -> {
                if (result instanceof Result.Loading) {
                    submitButton.setEnabled(false);
                    submitButton.setText("Checking...");
                } else if (result instanceof Result.Success) {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit OTP");
                    viewModel.setVerifiedResetOtp(otpCode);
                    if (requireActivity() instanceof NavigationHost) {
                        ((NavigationHost) requireActivity()).openSetNewPassword();
                    }
                } else if (result instanceof Result.Error) {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit OTP");
                    Toast.makeText(requireContext(), ((Result.Error<Void>) result).message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
