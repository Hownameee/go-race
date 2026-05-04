package com.grouprace.gorace;

import android.Manifest;
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
import com.grouprace.feature.club.ui.ClubsFragment;
import com.grouprace.feature.profile.ui.main.ProfileFragment;
import com.grouprace.feature.login.ui.LoginFragment;
import com.grouprace.feature.posts.ui.PostFragment;
import com.grouprace.feature.records.list.ui.RecordsFragment;
import com.grouprace.feature.register.ui.RegisterFragment;
import com.grouprace.feature.tracking.ui.NearbyRouteFragment;
import com.grouprace.feature.tracking.ui.TrackingFragment;
import com.grouprace.feature.map.ui.DrawRouteFragment;
import com.grouprace.feature.profile.ui.main.UserProfileFragment;
import com.grouprace.feature.posts.ui.PostDetailFragment;
import android.content.Intent;

import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    SessionManager sessionManager;

    private BottomNavigationView bottomNav;
    private MainViewModel viewModel;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_GoRace);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        handleNotificationIntent(getIntent());

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
                fragment = new ClubsFragment();
            } else if (itemId == R.id.nav_you) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment, false);
            }
            return true;
        });

        observeViewModel();

        requestNotificationPermissionIfNeeded();

        retryRegisterFcmToken();
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
            } else if (!isLoggedIn && !(currentFragment instanceof LoginFragment)
                    && !(currentFragment instanceof RegisterFragment)) {
                // If logged out and not on auth screens, go to login
                fragment = new LoginFragment();
            }
            if (fragment != null) {
                loadFragment(fragment, false);
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction transaction = 
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment);
        
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        
        transaction.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent == null || !intent.getBooleanExtra("from_notification", false)) {
            return;
        }

        int notificationId = intent.getIntExtra("id", -1);
        String type = intent.getStringExtra("type");

        if (notificationId != -1) {
            viewModel.markAsRead(notificationId);
        }

        if (type != null) {
            Fragment fragment = null;
            try {
                if ("follow".equals(type)) {
                    String actorIdStr = intent.getStringExtra("actor_id");
                    int actorId = actorIdStr != null ? Integer.parseInt(actorIdStr) : -1;
                    if (actorId != -1) {
                        fragment = UserProfileFragment.newInstance(actorId);
                    }
                } else if ("comment".equals(type) || "post".equals(type)) {
                    String activityIdStr = intent.getStringExtra("activity_id");
                    int activityId = activityIdStr != null ? Integer.parseInt(activityIdStr) : -1;
                    if (activityId != -1) {
                        fragment = PostDetailFragment.newInstance(activityId);
                    }
                }
            } catch (NumberFormatException e) {
                Log.e("MainActivity", "Error parsing notification extras: " + e.getMessage());
            }

            if (fragment != null) {
                boolean hasCurrentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null;
                loadFragment(fragment, hasCurrentFragment);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (viewModel != null) {
            viewModel.performGarbageCollection();
        }
    }

    private void retryRegisterFcmToken() {
        if (!sessionManager.isLoggedIn()) {
            return;
        }

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
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.POST_NOTIFICATIONS },
                    NOTIFICATION_PERMISSION_REQUEST_CODE);
        }
    }

}