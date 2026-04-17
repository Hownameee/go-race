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

import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private MainViewModel viewModel;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                fragment = new PlaceholderFragment();
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
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;
        Log.d("HANDLE INTENT", "intent" + intent);

        String type = intent.getStringExtra("type");

        Log.d("HANDLE INTENT", "type" + type);
        if (type == null) return;

        if ("follow".equals(type)) {

            String actorIdStr = intent.getStringExtra("actor_id");

            if (actorIdStr != null && !actorIdStr.isEmpty()) {
                try {
                    int userId = Integer.parseInt(actorIdStr);

                    ProfileFragment fragment =
                            ProfileFragment.newInstance(userId);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();

                } catch (Exception e) {
                    Log.e("MainActivity", "Invalid actor_id", e);
                }
            }

        } else if ("post".equals(type)) {

            String postIdStr = intent.getStringExtra("activity_id");

            if (postIdStr != null) {
                try {
                    int postId = Integer.parseInt(postIdStr);

                    Fragment fragment = new PostFragment();

                    bottomNav.setSelectedItemId(R.id.nav_home);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack("post_from_notification")
                            .commit();

                } catch (Exception e) {
                    Log.e("MainActivity", "Invalid postId", e);
                }
            }

        } else if ("comment".equals(type)) {

            String postIdStr = intent.getStringExtra("activity_id");

            if (postIdStr != null) {
                try {
                    int postId = Integer.parseInt(postIdStr);

                    Fragment fragment = CommentFragment.newInstance(postId);

                    bottomNav.setSelectedItemId(R.id.nav_home);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack("post_from_notification")
                            .commit();

                } catch (Exception e) {
                    Log.e("MainActivity", "Invalid postId", e);
                }
            }
        } else if ("system".equals(type)) {

            Fragment fragment = new NotificationFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack("system_notification")
                    .commit();
        }

        intent.removeExtra("notification_type");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

}
