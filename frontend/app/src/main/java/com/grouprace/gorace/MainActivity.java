package com.grouprace.gorace;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.grouprace.feature.login.ui.LoginFragment;
import com.grouprace.feature.profile.ui.EditProfileFragment;
import com.grouprace.feature.profile.ui.ProfileFragment;
import com.grouprace.feature.tracking.ui.TrackingFragment;
import com.grouprace.feature.posts.ui.PostFragment;
import com.grouprace.feature.register.ui.RegisterFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, new ProfileFragment())
                    .commit();
        }
    }
}
