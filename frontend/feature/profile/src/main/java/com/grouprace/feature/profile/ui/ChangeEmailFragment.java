package com.grouprace.feature.profile.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class ChangeEmailFragment extends Fragment {
    @Inject
    AppNavigator navigator;

    private ChangeEmailViewModel viewModel;

    public static ChangeEmailFragment newInstance() {
        return new ChangeEmailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ChangeEmailViewModel.class);
        if (!viewModel.isCurrentEmailVerified()) {
            navigator.openChangeEmailOtp(this);
            return;
        }

        ImageButton backButton = view.findViewById(R.id.change_email_back_button);
        EditText newEmailInput = view.findViewById(R.id.change_email_new_email_input);
        Button saveEmailButton = view.findViewById(R.id.change_email_send_otp_button);

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        saveEmailButton.setOnClickListener(v -> viewModel.confirmChange(newEmailInput.getText().toString())
                .observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Loading) {
                        saveEmailButton.setEnabled(false);
                        saveEmailButton.setText("Saving...");
                    } else if (result instanceof Result.Success) {
                        saveEmailButton.setEnabled(true);
                        saveEmailButton.setText("Update Email");
                        viewModel.resetFlow();
                        Toast.makeText(requireContext(), "Email changed successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else if (result instanceof Result.Error) {
                        saveEmailButton.setEnabled(true);
                        saveEmailButton.setText("Update Email");
                        Toast.makeText(requireContext(), ((Result.Error<Void>) result).message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}
