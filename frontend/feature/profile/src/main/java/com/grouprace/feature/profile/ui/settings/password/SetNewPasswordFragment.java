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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.profile.R;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SetNewPasswordFragment extends Fragment {
    @Inject
    AppNavigator navigator;

    private ChangePasswordViewModel viewModel;

    public static SetNewPasswordFragment newInstance() {
        return new SetNewPasswordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set_new_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ChangePasswordViewModel.class);
        setupTopBar(view);

        EditText newPasswordInput = view.findViewById(R.id.set_new_password_input);
        EditText confirmPasswordInput = view.findViewById(R.id.set_new_password_confirm_input);
        Button saveButton = view.findViewById(R.id.set_new_password_submit_button);

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        Runnable savePasswordAction = () -> viewModel.submitNewPassword(
                newPasswordInput.getText().toString(),
                confirmPasswordInput.getText().toString()
        ).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                saveButton.setEnabled(false);
                saveButton.setText("Saving...");
            } else if (result instanceof Result.Success) {
                saveButton.setEnabled(true);
                saveButton.setText("Save Password");
                boolean shouldReturnToLogin = viewModel.getResetEmail() != null;
                viewModel.clearFlowState();
                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                if (shouldReturnToLogin) {
                    navigator.openLogin(this);
                } else {
                    // Pop the entire change-password flow back to whoever opened it (Settings).
                    requireActivity().getSupportFragmentManager().popBackStack(
                            AppNavigator.PASSWORD_FLOW_BACK_TAG,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            } else if (result instanceof Result.Error) {
                saveButton.setEnabled(true);
                saveButton.setText("Save Password");
                Toast.makeText(requireContext(), ((Result.Error<Void>) result).message, Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> savePasswordAction.run());
        confirmPasswordInput.setOnEditorActionListener((v, actionId, event) -> handleSubmitAction(actionId, event, savePasswordAction));
    }

    private void setupTopBar(View view) {
        String title = viewModel.getResetEmail() != null ? "Set New Password" : "Change Password";
        TopAppBarHelper.setupTopAppBar(view, new TopAppBarConfig.Builder()
                .setTitle(title)
                .setLeftIcon(com.grouprace.core.system.R.drawable.ic_back, v -> requireActivity().onBackPressed())
                .build());
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
