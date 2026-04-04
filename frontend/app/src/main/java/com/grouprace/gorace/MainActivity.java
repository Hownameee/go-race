package com.grouprace.gorace;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grouprace.core.network.utils.SessionManager;
import com.grouprace.core.system.ui.PlaceholderFragment;
import com.grouprace.feature.profile.ui.ProfileFragment;
import com.grouprace.feature.login.ui.LoginFragment;
import com.grouprace.feature.posts.ui.PostFragment;
import com.grouprace.feature.records.list.ui.RecordsFragment;
import com.grouprace.feature.register.ui.RegisterFragment;
import com.grouprace.feature.tracking.ui.TrackingFragment;

import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    SessionManager sessionManager; // Keep for some direct checks or remove if fully reactive

    private BottomNavigationView bottomNav;
    private MainViewModel viewModel;

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
                fragment = new RecordsFragment();
            } else if (itemId == R.id.nav_record) {
                fragment = new RecordsFragment();
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
}
