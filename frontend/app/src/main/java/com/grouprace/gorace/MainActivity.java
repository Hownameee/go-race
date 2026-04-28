package com.grouprace.gorace;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.TokenManager;
import com.grouprace.core.network.utils.SessionManager;
import com.grouprace.core.system.ui.PlaceholderFragment;
import com.grouprace.feature.login.ui.LoginViewModel;
import com.grouprace.feature.notification.ui.NotificationFragment;
import com.grouprace.feature.posts.ui.CommentFragment;
import com.grouprace.feature.profile.ui.ProfileFragment;
import com.grouprace.feature.login.ui.LoginFragment;
import com.grouprace.feature.posts.ui.PostFragment;
import com.grouprace.feature.records.list.ui.RecordsFragment;
import com.grouprace.feature.register.ui.RegisterFragment;
import com.grouprace.feature.tracking.ui.NearbyRouteFragment;
import com.grouprace.feature.tracking.ui.TrackingFragment;
import com.grouprace.feature.map.ui.DrawRouteFragment;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;

import androidx.core.splashscreen.SplashScreen;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private MainViewModel viewModel;
    private boolean handledIntent = false;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragment = new PostFragment();
            } else if (itemId == R.id.nav_maps) {
                fragment = new DrawRouteFragment();
            } else if (itemId == R.id.nav_record) {
                fragment = new NearbyRouteFragment();
            } else if (itemId == R.id.nav_clubs) {
                fragment = new PlaceholderFragment();
            } else if (itemId == R.id.nav_you) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
            }
            return true;
        });

        observeViewModel();

        requestNotificationPermissionIfNeeded();

        retryRegisterFcmToken();

        handleIntent(getIntent());
    }

    private void observeViewModel() {
        viewModel.getIsLoggedIn().observe(this, isLoggedIn -> {
            bottomNav.setVisibility(isLoggedIn ? android.view.View.VISIBLE : android.view.View.GONE);

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            Fragment fragment = null;
            if (currentFragment == null) {
                if (isLoggedIn) {
                    fragment = new PostFragment();
                } else {
                    fragment = new LoginFragment();
                }
            } else if (!isLoggedIn && !(currentFragment instanceof LoginFragment) && !(currentFragment instanceof RegisterFragment)) {
                // If logged out and not on auth screens, go to login
                fragment = new LoginFragment();
            }
            if (fragment != null) {
                loadFragment(fragment);
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void retryRegisterFcmToken() {
        String token = TokenManager.getToken(this);
        boolean isRegistered = TokenManager.isRegistered(this);

        if (token != null && !isRegistered) {
            Log.d("FCM", "Retry register token...");

            LoginViewModel viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

            viewModel.registerDeviceToken(token)
                    .observe(this, result -> {
                        if (result instanceof Result.Success) {
                            TokenManager.markRegistered(this);
                            Log.d("FCM", "Retry SUCCESS");
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        retryRegisterFcmToken();

        handleIntent(getIntent());
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE);
        }
    }

    private int safeParse(String value) {
        try {
            return (value == null || value.isEmpty())
                    ? -1
                    : Integer.parseInt(value);
        } catch (Exception e) {
            return -1;
        }
    }

    private void handleIntent(Intent intent) {

        if (intent == null || intent.getExtras() == null) return;
        if (handledIntent) return;
        handledIntent = true;

        String type = intent.getStringExtra("type");
        if (type == null) return;

        int postId = safeParse(intent.getStringExtra("activity_id"));
        int userId = safeParse(intent.getStringExtra("actor_id"));

        Fragment current = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (current == null) {
            current = new PostFragment();
            loadFragment(current);
        }

        switch (type) {

            case "follow":
                if (userId > 0) {
                    loadFragment(ProfileFragment.newInstance(userId));
                }
                break;

            case "comment":
                if (postId > 0) {
                    loadFragment(CommentFragment.newInstance(postId));
                }
                break;

            case "post":
                loadFragment(new PostFragment());
                bottomNav.setSelectedItemId(R.id.nav_home);
                break;

            case "system":
                loadFragment(new NotificationFragment());
                break;
        }
    }

}