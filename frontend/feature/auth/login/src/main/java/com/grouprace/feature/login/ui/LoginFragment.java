package com.grouprace.feature.login.ui;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.grouprace.core.network.utils.SessionManager;
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
                        android.util.Log.d("DEBUG_API", "Dữ liệu trả về: " + dataObj.toString());

                        if (dataObj != null && dataObj.has("token")) {
                            String token = dataObj.get("token").getAsString();

                            // Lưu token vào SessionManager như bước trước
                            SessionManager sessionManager = new SessionManager(requireContext());
                            sessionManager.saveAuthToken(token);

                            // TODO: Chuyển trang
                            try {
                                // Tìm class MainActivity thông qua đường dẫn String
                                Class<?> mainActivityClass = Class.forName("com.grouprace.gorace.MainActivity");

                                Intent intent = new Intent(requireActivity(), mainActivityClass);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                requireActivity().finish();

                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Lỗi chuyển trang!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}