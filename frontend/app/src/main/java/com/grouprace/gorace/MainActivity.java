package com.grouprace.gorace;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grouprace.core.network.utils.SessionManager;
import com.grouprace.core.system.ui.PlaceholderFragment;
import com.grouprace.feature.login.ui.LoginFragment;
import com.grouprace.feature.posts.ui.MyPostsFragment;
import com.grouprace.feature.posts.ui.PostFragment;
import com.grouprace.feature.profile.ui.ChangeEmailFragment;
import com.grouprace.feature.profile.ui.ChangeEmailOtpFragment;
import com.grouprace.feature.profile.ui.ChangePasswordFragment;
import com.grouprace.feature.profile.ui.EditProfileFragment;
import com.grouprace.feature.profile.ui.PasswordResetOtpFragment;
import com.grouprace.feature.profile.ui.PasswordResetRequestFragment;
import com.grouprace.feature.profile.ui.ProfileComingSoonFragment;
import com.grouprace.feature.profile.ui.ProfileFragment;
import com.grouprace.feature.profile.ui.ProfileSettingsFragment;
import com.grouprace.feature.profile.ui.SetNewPasswordFragment;
import com.grouprace.feature.records.list.ui.RecordsFragment;
import com.grouprace.feature.register.ui.RegisterFragment;
import com.grouprace.feature.tracking.ui.TrackingFragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity
        implements
        LoginFragment.NavigationHost,
        RegisterFragment.NavigationHost,
        ProfileFragment.NavigationHost,
        ProfileSettingsFragment.NavigationHost,
        ChangeEmailFragment.NavigationHost,
        ChangePasswordFragment.NavigationHost,
        PasswordResetRequestFragment.NavigationHost,
        PasswordResetOtpFragment.NavigationHost {

    @Inject
    SessionManager sessionManager;

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        boolean isLoggedIn = sessionManager != null && sessionManager.isLoggedIn();
        bottomNav.setVisibility(isLoggedIn ? android.view.View.VISIBLE : android.view.View.GONE);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragment = new PostFragment();
            } else if (itemId == R.id.nav_maps) {
                fragment = new RecordsFragment();
            } else if (itemId == R.id.nav_record) {
                fragment = new TrackingFragment();
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

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment == null) {
            if (isLoggedIn) {
                showAuthenticatedHome();
            } else {
                showLoginEntry();
            }
        }
    }

    @Override
    public void openRegister() {
        bottomNav.setVisibility(android.view.View.GONE);
        loadAuthRootFragment(new RegisterFragment());
    }

    @Override
    public void openForgotPassword() {
        bottomNav.setVisibility(android.view.View.GONE);
        loadSubFragment(PasswordResetRequestFragment.newInstance());
    }

    @Override
    public void openLogin() {
        bottomNav.setVisibility(android.view.View.GONE);
        loadAuthRootFragment(new LoginFragment());
    }

    @Override
    public void openEditProfile() {
        bottomNav.setVisibility(android.view.View.VISIBLE);
        loadSubFragment(new EditProfileFragment());
    }

    @Override
    public void openProfileSettings() {
        bottomNav.setVisibility(android.view.View.VISIBLE);
        loadSubFragment(ProfileSettingsFragment.newInstance());
    }

    @Override
    public void openChangeEmail() {
        bottomNav.setVisibility(android.view.View.VISIBLE);
        loadSubFragment(ChangeEmailFragment.newInstance());
    }

    @Override
    public void openChangeEmailOtp() {
        bottomNav.setVisibility(android.view.View.VISIBLE);
        loadSubFragment(ChangeEmailOtpFragment.newInstance());
    }

    @Override
    public void openChangePassword() {
        bottomNav.setVisibility(android.view.View.VISIBLE);
        loadSubFragment(ChangePasswordFragment.newInstance());
    }

    @Override
    public void openPasswordResetRequest() {
        bottomNav.setVisibility(android.view.View.VISIBLE);
        loadSubFragment(PasswordResetRequestFragment.newInstance());
    }

    @Override
    public void openPasswordResetOtp() {
        bottomNav.setVisibility(android.view.View.VISIBLE);
        loadSubFragment(PasswordResetOtpFragment.newInstance());
    }

    @Override
    public void openSetNewPassword() {
        bottomNav.setVisibility(android.view.View.VISIBLE);
        loadSubFragment(SetNewPasswordFragment.newInstance());
    }

    @Override
    public void openComingSoon(String title) {
        bottomNav.setVisibility(android.view.View.VISIBLE);
        loadSubFragment(ProfileComingSoonFragment.newInstance(title));
    }

    @Override
    public void openProfileComingSoon(String title) {
        bottomNav.setVisibility(android.view.View.VISIBLE);
        loadSubFragment(ProfileComingSoonFragment.newInstance(title));
    }

    @Override
    public void openMyPosts() {
        bottomNav.setVisibility(android.view.View.VISIBLE);
        loadSubFragment(MyPostsFragment.newInstance());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void loadSubFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadAuthRootFragment(Fragment fragment) {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(
                    null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
            );
        }
        loadFragment(fragment);
    }

    private void showAuthenticatedHome() {
        bottomNav.setVisibility(android.view.View.VISIBLE);
        bottomNav.setSelectedItemId(R.id.nav_home);
        loadFragment(new PostFragment());
    }

    private void showLoginEntry() {
        bottomNav.setVisibility(android.view.View.GONE);
        loadFragment(new LoginFragment());
    }
}
