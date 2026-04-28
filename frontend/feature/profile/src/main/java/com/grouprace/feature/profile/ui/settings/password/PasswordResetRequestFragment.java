package com.grouprace.feature.profile.ui.settings.password;

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
public class PasswordResetRequestFragment extends Fragment {
    @Inject
    AppNavigator navigator;

    private ChangePasswordViewModel viewModel;

    public static PasswordResetRequestFragment newInstance() {
        return new PasswordResetRequestFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password_reset_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ChangePasswordViewModel.class);
        viewModel.clearFlowState();

        ImageButton backButton = view.findViewById(R.id.password_reset_request_back_button);
        EditText emailInput = view.findViewById(R.id.password_reset_request_email_input);
        Button sendButton = view.findViewById(R.id.password_reset_request_send_button);

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        Runnable sendOtpAction = () -> viewModel.requestPasswordResetOtp(emailInput.getText().toString())
                .observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Loading) {
                        sendButton.setEnabled(false);
                        sendButton.setText("Sending...");
                    } else if (result instanceof Result.Success) {
                        sendButton.setEnabled(true);
                        sendButton.setText("Send OTP");
                        Toast.makeText(requireContext(), "OTP sent successfully", Toast.LENGTH_SHORT).show();
                        navigator.openPasswordResetOtp(this);
                    } else if (result instanceof Result.Error) {
                        sendButton.setEnabled(true);
                        sendButton.setText("Send OTP");
                        Toast.makeText(requireContext(), ((Result.Error<Void>) result).message, Toast.LENGTH_SHORT).show();
                    }
                });

        sendButton.setOnClickListener(v -> sendOtpAction.run());
        emailInput.setOnEditorActionListener((v, actionId, event) -> handleSubmitAction(actionId, event, sendOtpAction));
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
}
