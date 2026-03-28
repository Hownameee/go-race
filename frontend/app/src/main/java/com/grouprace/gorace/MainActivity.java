package com.grouprace.gorace;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.grouprace.feature.notification.ui.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grouprace.core.system.ui.PlaceholderFragment;
import com.grouprace.feature.tracking.ui.TrackingFragment;
import com.grouprace.feature.posts.ui.PostFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new PostFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragment = new PostFragment();
            } else if (itemId == R.id.nav_maps) {
                fragment = new PlaceholderFragment();
            } else if (itemId == R.id.nav_record) {
                fragment = new TrackingFragment();
            } else if (itemId == R.id.nav_clubs) {
                fragment = new PlaceholderFragment();
            } else if (itemId == R.id.nav_you) {
                fragment = new PlaceholderFragment();
            } else if (itemId == R.id.nav_notifications) {
                fragment = new NotificationFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
