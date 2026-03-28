package com.grouprace.feature.register.ui;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {
    private EditText editUsername, editFullname, editEmail, editBirthdate, editPassword, editConfirmPassword;
    private Button buttonRegister, buttonBack, buttonGoToLogin;
    private RegisterViewModel viewModel;

    public static RegisterFragment newInstance() { return new RegisterFragment(); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    // https://developer.android.com/guide/fragments/lifecycle
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
        editUsername = view.findViewById(R.id.username_edit_text);
        editFullname = view.findViewById(R.id.fullname_edit_text);
        editEmail = view.findViewById(R.id.email_edit_text);
        editBirthdate = view.findViewById(R.id.birthdate_edit_text);
        editPassword = view.findViewById(R.id.password_edit_text);
        editConfirmPassword = view.findViewById(R.id.confirm_password_edit_text);

        buttonBack = view.findViewById(R.id.back_button);
        buttonRegister = view.findViewById(R.id.register_button);
        buttonGoToLogin = view.findViewById(R.id.goto_login_button);
    }

    private void setupListeners() {
        buttonBack.setOnClickListener(v -> requireActivity().onBackPressed());
        buttonGoToLogin.setOnClickListener(v -> requireActivity().onBackPressed());
        buttonRegister.setOnClickListener(this::register);
    }

    private void register(View view) {
        String username = editUsername.getText().toString().trim();
        String fullname = editFullname.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String birthdate = editBirthdate.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        viewModel.register(username, fullname, email, birthdate, password, confirmPassword);
    }
}