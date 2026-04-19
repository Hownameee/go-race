package com.grouprace.feature.register.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.messaging.FirebaseMessaging;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.TokenManager;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.network.config.GoogleAuthConfig;
import com.grouprace.core.network.model.auth.GoogleAuthResponse;
import com.grouprace.core.network.model.auth.GoogleProfileInfo;
import com.grouprace.core.system.ui.DatePickerHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {
    private static final String ARG_PREFILL_FULLNAME = "prefill_fullname";
    private static final String ARG_PREFILL_EMAIL = "prefill_email";
    private static final String ARG_GOOGLE_ID_TOKEN = "google_id_token";

    @Inject
    AppNavigator navigator;

    private EditText editUsername;
    private EditText editFullname;
    private EditText editEmail;
    private EditText editBirthdate;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private TextView fullnameLabel;
    private TextView emailLabel;
    private TextView passwordLabel;
    private TextView confirmPasswordLabel;
    private Button buttonRegister;
    private Button buttonGoogleRegister;
    private Button buttonGoToLogin;
    private CredentialManager credentialManager;
    private RegisterViewModel viewModel;
    private boolean isGooglePrefillMode;
    private String pendingGoogleIdToken;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    public static RegisterFragment newInstance(String fullname, String email) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PREFILL_FULLNAME, fullname);
        args.putString(ARG_PREFILL_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    public static RegisterFragment newInstance(String fullname, String email, String googleIdToken) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PREFILL_FULLNAME, fullname);
        args.putString(ARG_PREFILL_EMAIL, email);
        args.putString(ARG_GOOGLE_ID_TOKEN, googleIdToken);
        fragment.setArguments(args);
        return fragment;
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
        credentialManager = CredentialManager.create(requireContext());
        initViews(view);
        setupListeners();
        observeViewModel();

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
        fullnameLabel = view.findViewById(R.id.register_fullname_label);
        emailLabel = view.findViewById(R.id.register_email_label);
        passwordLabel = view.findViewById(R.id.register_password_label);
        confirmPasswordLabel = view.findViewById(R.id.register_confirm_password_label);
        buttonRegister = view.findViewById(R.id.register_submit_button);
        buttonGoogleRegister = view.findViewById(R.id.register_google_button);
        buttonGoToLogin = view.findViewById(R.id.register_goto_login_button);
        applyPrefillArguments();
    }

    private void applyPrefillArguments() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        String prefillFullname = args.getString(ARG_PREFILL_FULLNAME);
        String prefillEmail = args.getString(ARG_PREFILL_EMAIL);
        pendingGoogleIdToken = args.getString(ARG_GOOGLE_ID_TOKEN);

        isGooglePrefillMode =
                prefillFullname != null && !prefillFullname.trim().isEmpty()
                        && prefillEmail != null && !prefillEmail.trim().isEmpty()
                        && pendingGoogleIdToken != null && !pendingGoogleIdToken.trim().isEmpty();

        if (prefillFullname != null && !prefillFullname.trim().isEmpty()) {
            editFullname.setText(prefillFullname);
        }

        if (prefillEmail != null && !prefillEmail.trim().isEmpty()) {
            editEmail.setText(prefillEmail);
        }

        if (isGooglePrefillMode) {
            enterGooglePrefillMode(prefillFullname, prefillEmail, pendingGoogleIdToken);
        }
    }

    private void enterGooglePrefillMode(String fullname, String email, String googleIdToken) {
        isGooglePrefillMode = googleIdToken != null && !googleIdToken.trim().isEmpty();
        pendingGoogleIdToken = googleIdToken;

        if (fullname != null && !fullname.trim().isEmpty()) {
            editFullname.setText(fullname);
        }

        if (email != null && !email.trim().isEmpty()) {
            editEmail.setText(email);
        }

        editFullname.setEnabled(false);
        editFullname.setFocusable(false);
        editFullname.setClickable(false);

        editEmail.setEnabled(false);
        editEmail.setFocusable(false);
        editEmail.setClickable(false);

        passwordLabel.setVisibility(View.GONE);
        editPassword.setVisibility(View.GONE);
        confirmPasswordLabel.setVisibility(View.GONE);
        editConfirmPassword.setVisibility(View.GONE);
        buttonRegister.setVisibility(View.GONE);
        buttonGoogleRegister.setText("Complete Google Sign Up");
    }

    private void setupListeners() {
        buttonGoToLogin.setOnClickListener(v -> {
            navigator.openLogin(this);
        });

        DatePickerHelper.attachDatePicker(this, editBirthdate);
        buttonRegister.setOnClickListener(v -> performRegister());
        buttonGoogleRegister.setOnClickListener(v -> {
            if (isGooglePrefillMode) {
                completeGoogleSignUp();
            } else {
                startGoogleSignIn();
            }
        });
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

    private void observeViewModel() {
        viewModel.getGoogleAuthState().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                buttonGoogleRegister.setEnabled(false);
                buttonGoogleRegister.setText("Connecting...");
            } else if (result instanceof Result.Success) {
                GoogleAuthResponse response = ((Result.Success<GoogleAuthResponse>) result).data;
                if (response != null && response.isRequiresProfileCompletion()) {
                    GoogleProfileInfo profile = response.getProfile();
                    String fullname = profile != null ? profile.getFullname() : null;
                    String email = profile != null ? profile.getEmail() : null;

                    enterGooglePrefillMode(fullname, email, pendingGoogleIdToken);
                    resetGoogleButtonState();
                    Toast.makeText(requireContext(), "Please complete username and birthdate to finish Google sign up.", Toast.LENGTH_LONG).show();
                    return;
                }

                resetGoogleButtonState();
                Toast.makeText(requireContext(), "Google sign up successful!", Toast.LENGTH_SHORT).show();
                handleFcmToken();
                openMainActivity();
            } else if (result instanceof Result.Error) {
                resetGoogleButtonState();
                String errorMsg = ((Result.Error<GoogleAuthResponse>) result).message;
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
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

    private void startGoogleSignIn() {
        buttonGoogleRegister.setEnabled(false);
        buttonGoogleRegister.setText("Connecting...");

        GetSignInWithGoogleOption googleOption =
                new GetSignInWithGoogleOption.Builder(GoogleAuthConfig.WEB_CLIENT_ID).build();
        GetCredentialRequest request =
                new GetCredentialRequest.Builder().addCredentialOption(googleOption).build();

        credentialManager.getCredentialAsync(
                requireContext(),
                request,
                null,
                ContextCompat.getMainExecutor(requireContext()),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleGoogleCredentialResult(result);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        resetGoogleButtonState();
                        Toast.makeText(requireContext(), "Google sign up failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void completeGoogleSignUp() {
        buttonGoogleRegister.setEnabled(false);
        buttonGoogleRegister.setText("Connecting...");

        String username = editUsername.getText().toString().trim();
        String birthdate = editBirthdate.getText().toString().trim();
        viewModel.onGoogleIdTokenReceived(pendingGoogleIdToken, username, birthdate);
    }

    private void handleGoogleCredentialResult(GetCredentialResponse result) {
        Credential credential = result.getCredential();

        if (!(credential instanceof CustomCredential)) {
            resetGoogleButtonState();
            Toast.makeText(requireContext(), "Unsupported Google credential.", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomCredential customCredential = (CustomCredential) credential;
        if (!GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(customCredential.getType())) {
            resetGoogleButtonState();
            Toast.makeText(requireContext(), "Invalid Google credential type.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            GoogleIdTokenCredential googleCredential =
                    GoogleIdTokenCredential.createFrom(customCredential.getData());
            pendingGoogleIdToken = googleCredential.getIdToken();

            String username = editUsername.getText().toString().trim();
            String birthdate = editBirthdate.getText().toString().trim();
            viewModel.onGoogleIdTokenReceived(pendingGoogleIdToken, username, birthdate);
        } catch (RuntimeException e) {
            resetGoogleButtonState();
            Toast.makeText(requireContext(), "Cannot parse Google token.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFcmToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FCM", "Get token FAILED");
                return;
            }

            String token = task.getResult();
            TokenManager.saveToken(requireContext(), token);

            viewModel.registerDeviceToken(token)
                    .observe(getViewLifecycleOwner(), result -> {
                        if (result instanceof Result.Loading) {
                            Log.d("FCM", "Registering token...");
                        } else if (result instanceof Result.Success) {
                            Log.d("FCM", "Token registered SUCCESS");
                            TokenManager.markRegistered(requireContext());
                            FirebaseMessaging.getInstance().subscribeToTopic("system-notifications");
                        } else if (result instanceof Result.Error) {
                            String error = ((Result.Error<?>) result).message;
                            Log.e("FCM", "Register FAILED: " + error);
                        }
                    });
        });
    }

    private void resetGoogleButtonState() {
        buttonGoogleRegister.setEnabled(true);
        buttonGoogleRegister.setText(isGooglePrefillMode ? "Complete Google Sign Up" : "Sign up with Google");
    }

    private void openMainActivity() {
        try {
            Class<?> mainActivityClass = Class.forName("com.grouprace.gorace.MainActivity");
            Intent intent = new Intent(requireActivity(), mainActivityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        } catch (ClassNotFoundException e) {
            Toast.makeText(requireContext(), "Navigation error!", Toast.LENGTH_SHORT).show();
        }
    }
}
