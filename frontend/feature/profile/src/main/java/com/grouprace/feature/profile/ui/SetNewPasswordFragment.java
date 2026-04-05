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
public class SetNewPasswordFragment extends Fragment {
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

        ImageButton backButton = view.findViewById(R.id.set_new_password_back_button);
        TextView titleView = view.findViewById(R.id.set_new_password_title);
        EditText newPasswordInput = view.findViewById(R.id.set_new_password_input);
        EditText confirmPasswordInput = view.findViewById(R.id.set_new_password_confirm_input);
        Button saveButton = view.findViewById(R.id.set_new_password_submit_button);

        if (viewModel.getResetEmail() != null) {
            titleView.setText("Set New Password");
        }

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> viewModel.submitNewPassword(
                newPasswordInput.getText().toString(),
                confirmPasswordInput.getText().toString()
        ).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                saveButton.setEnabled(false);
                saveButton.setText("Saving...");
            } else if (result instanceof Result.Success) {
                saveButton.setEnabled(true);
                saveButton.setText("Save Password");
                viewModel.clearFlowState();
                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack(null,
                        androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else if (result instanceof Result.Error) {
                saveButton.setEnabled(true);
                saveButton.setText("Save Password");
                Toast.makeText(requireContext(), ((Result.Error<Void>) result).message, Toast.LENGTH_SHORT).show();
            }
        }));
    }
}
