package com.grouprace.feature.login.ui;

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
import com.grouprace.core.network.model.auth.GoogleProfileInfo;
import com.grouprace.core.network.model.auth.GoogleAuthResponse;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
  @Inject
  AppNavigator navigator;

  private EditText editEmail;
  private EditText editPassword;
  private Button buttonLogin;

  private Button buttonGoogleLogin;
  private CredentialManager credentialManager;
  private String pendingGoogleIdToken;

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
    editEmail = view.findViewById(R.id.login_email_input);
    editPassword = view.findViewById(R.id.login_password_input);
    buttonLogin = view.findViewById(R.id.login_submit_button);
    buttonGoogleLogin = view.findViewById(R.id.login_google_button);
    buttonGoToRegister = view.findViewById(R.id.login_goto_register_button);
    resetPasswordButton = view.findViewById(R.id.login_reset_password_button);
  }

  private void setupListeners() {
    buttonGoToRegister.setOnClickListener(v -> {
      navigator.openRegister(this);
    });
    resetPasswordButton.setOnClickListener(v -> {
      navigator.openForgotPassword(this);
    });

    buttonLogin.setOnClickListener(v -> performLogin());

    buttonGoogleLogin.setOnClickListener(v -> startGoogleSignIn());

    editPassword.setOnEditorActionListener((v, actionId, event) -> {
      boolean isDoneAction = actionId == EditorInfo.IME_ACTION_DONE;
      boolean isEnterKey = event != null
        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
        && event.getAction() == KeyEvent.ACTION_DOWN;

      if (isDoneAction || isEnterKey) {
        performLogin();
        return true;
      }
      return false;
    });
  }

  private void observeViewModel() {
    viewModel.getGoogleAuthState().observe(getViewLifecycleOwner(), result -> {
      if (result instanceof Result.Loading) {
        buttonGoogleLogin.setEnabled(false);
        buttonGoogleLogin.setText("Connecting...");
      } else if (result instanceof Result.Success) {
        resetGoogleButtonState();

        GoogleAuthResponse response = ((Result.Success<GoogleAuthResponse>) result).data;
        if (response != null && response.isRequiresProfileCompletion()) {
          GoogleProfileInfo profile = response.getProfile();
          String fullname = profile != null ? profile.getFullname() : null;
          String email = profile != null ? profile.getEmail() : null;
          Toast.makeText(requireContext(), "Please complete username and birthdate in Register screen.", Toast.LENGTH_LONG).show();
          navigator.openRegister(this, fullname, email, pendingGoogleIdToken);
          return;
        }

        Toast.makeText(requireContext(), "Google login successful!", Toast.LENGTH_SHORT).show();
        handleFcmToken();
        openMainActivity();
      } else if (result instanceof Result.Error) {
        resetGoogleButtonState();
        String errorMsg = ((Result.Error<GoogleAuthResponse>) result).message;
        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
      }
    });
  }

  private void startGoogleSignIn() {
    if (!GoogleAuthConfig.isConfigured()) {
      Toast.makeText(requireContext(), "Google OAuth is not configured.", Toast.LENGTH_LONG).show();
      return;
    }

    buttonGoogleLogin.setEnabled(false);
    buttonGoogleLogin.setText("Connecting...");

    GetSignInWithGoogleOption googleOption = new GetSignInWithGoogleOption.Builder(GoogleAuthConfig.WEB_CLIENT_ID).build();
    GetCredentialRequest request = new GetCredentialRequest.Builder().addCredentialOption(googleOption).build();

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
          Log.e("GoogleOAuth", "Credential error: "
            + e.getClass().getSimpleName() + " - " + e.getMessage(), e);
          buttonGoogleLogin.setEnabled(true);
          buttonGoogleLogin.setText("Continue with Google");
          Toast.makeText(requireContext(), "Google sign in failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    );
  }

  private void handleGoogleCredentialResult(GetCredentialResponse result) {
    Credential credential = result.getCredential();

    if (!(credential instanceof CustomCredential)) {
      buttonGoogleLogin.setEnabled(true);
      buttonGoogleLogin.setText("Continue with Google");
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
      viewModel.onGoogleIdTokenReceived(pendingGoogleIdToken);
    } catch (RuntimeException e) {
      resetGoogleButtonState();
      Toast.makeText(requireContext(), "Cannot parse Google token.", Toast.LENGTH_SHORT).show();
    }
  }

  private void performLogin() {
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
        handleFcmToken();
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
            FirebaseMessaging.getInstance()
              .subscribeToTopic("system-notifications");
          } else if (result instanceof Result.Error) {
            String error = ((Result.Error<?>) result).message;
            Log.e("FCM", "Register FAILED: " + error);
          }
        });
    });
  }

  private void resetGoogleButtonState() {
    buttonGoogleLogin.setEnabled(true);
    buttonGoogleLogin.setText("Continue with Google");
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
