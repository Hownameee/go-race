package com.grouprace.feature.login.ui;

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

import com.google.gson.JsonObject;
import com.grouprace.feature.login.ui.R;

import dagger.hilt.EntryPoint;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    private EditText editEmail, editPassword;
    private Button buttonLogin, buttonBack, buttonGoToRegister;

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
        // TODO: Use the ViewModel
        initViews(view);
        setupListeners();

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(View view) {
        editEmail = view.findViewById(R.id.email_edit_text);
        editPassword = view.findViewById(R.id.password_edit_text);

        buttonLogin = view.findViewById(R.id.login_button);
        buttonBack = view.findViewById(R.id.back_button);
        buttonGoToRegister = view.findViewById(R.id.goto_register_button);
    }

    private void setupListeners() {
        buttonBack.setOnClickListener(v -> requireActivity().onBackPressed());
        buttonGoToRegister.setOnClickListener(v -> requireActivity().onBackPressed());

        buttonLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            viewModel.login(email, password).observe(getViewLifecycleOwner(), response -> {
                if (response != null) {
                    if (response.isSuccess()) {
                        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show();

                        JsonObject dataObj = response.getData();
                        if (dataObj != null && dataObj.has("token")) {
                            String token = dataObj.get("token").getAsString();

                            // Lưu token vào SessionManager như bước trước
                            // TODO: Chuyển trang
                        }
                    } else {
                        Toast.makeText(requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}