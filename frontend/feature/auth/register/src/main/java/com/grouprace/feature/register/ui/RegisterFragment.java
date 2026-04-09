package com.grouprace.feature.register.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.system.ui.DatePickerHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {
    @Inject
    AppNavigator navigator;

    private EditText editUsername;
    private EditText editFullname;
    private EditText editEmail;
    private EditText editBirthdate;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private Button buttonRegister;
    private Button buttonGoToLogin;
    private RegisterViewModel viewModel;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        initViews(view);
        setupListeners();

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(View view) {
        editUsername = view.findViewById(R.id.register_username_input);
        editFullname = view.findViewById(R.id.register_fullname_input);
        editEmail = view.findViewById(R.id.register_email_input);
        editBirthdate = view.findViewById(R.id.register_birthdate_input);
        editPassword = view.findViewById(R.id.register_password_input);
        editConfirmPassword = view.findViewById(R.id.register_confirm_password_input);
        buttonRegister = view.findViewById(R.id.register_submit_button);
        buttonGoToLogin = view.findViewById(R.id.register_goto_login_button);
    }

    private void setupListeners() {
        buttonGoToLogin.setOnClickListener(v -> {
            navigator.openLogin(this);
        });

        DatePickerHelper.attachDatePicker(this, editBirthdate);
        buttonRegister.setOnClickListener(v -> performRegister());
        editConfirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            boolean isDoneAction = actionId == EditorInfo.IME_ACTION_DONE;
            boolean isEnterKey = event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN;

            if (isDoneAction || isEnterKey) {
                performRegister();
                return true;
            }
            return false;
        });
    }

    private void performRegister() {
        String username = editUsername.getText().toString().trim();
        String fullname = editFullname.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String birthdate = editBirthdate.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        viewModel.register(username, fullname, email, birthdate, password, confirmPassword)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.Loading) {
                        buttonRegister.setEnabled(false);
                        buttonRegister.setText("Registering...");
                    } else if (result instanceof Result.Success) {
                        buttonRegister.setEnabled(true);
                        buttonRegister.setText("Register");

                        Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();

                        navigator.openLogin(this);
                    } else if (result instanceof Result.Error) {
                        buttonRegister.setEnabled(true);
                        buttonRegister.setText("Register");

                        String errorMsg = ((Result.Error<Void>) result).message;
                        Toast.makeText(requireContext(), "Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
