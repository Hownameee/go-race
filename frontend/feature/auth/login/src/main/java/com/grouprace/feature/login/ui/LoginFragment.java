package com.grouprace.feature.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    public interface NavigationHost {
        void openRegister();
        void openForgotPassword();
    }

    private EditText editEmail;
    private EditText editPassword;
    private Button buttonLogin;
    private Button buttonGoToRegister;
    private Button resetPasswordButton;
    private LoginViewModel viewModel;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initViews(view);
        setupListeners();

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(View view) {
        editEmail = view.findViewById(R.id.login_email_input);
        editPassword = view.findViewById(R.id.login_password_input);
        buttonLogin = view.findViewById(R.id.login_submit_button);
        buttonGoToRegister = view.findViewById(R.id.login_goto_register_button);
        resetPasswordButton = view.findViewById(R.id.login_reset_password_button);
    }

    private void setupListeners() {
        buttonGoToRegister.setOnClickListener(v -> {
            if (requireActivity() instanceof NavigationHost) {
                ((NavigationHost) requireActivity()).openRegister();
            }
        });
        resetPasswordButton.setOnClickListener(v -> {
            if (requireActivity() instanceof NavigationHost) {
                ((NavigationHost) requireActivity()).openForgotPassword();
            }
        });

        buttonLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            viewModel.login(email, password).observe(getViewLifecycleOwner(), result -> {
                if (result instanceof Result.Loading) {
                    buttonLogin.setEnabled(false);
                    buttonLogin.setText("Logging in...");
                } else if (result instanceof Result.Success) {
                    buttonLogin.setEnabled(true);
                    buttonLogin.setText("Login");

                    Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show();

                    try {
                        Class<?> mainActivityClass = Class.forName("com.grouprace.gorace.MainActivity");
                        Intent intent = new Intent(requireActivity(), mainActivityClass);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    } catch (ClassNotFoundException e) {
                        Toast.makeText(requireContext(), "Navigation error!", Toast.LENGTH_SHORT).show();
                    }
                } else if (result instanceof Result.Error) {
                    buttonLogin.setEnabled(true);
                    buttonLogin.setText("Login");

                    String errorMsg = ((Result.Error<Void>) result).message;
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
