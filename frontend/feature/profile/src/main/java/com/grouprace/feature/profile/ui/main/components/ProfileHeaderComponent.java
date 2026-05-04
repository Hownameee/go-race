package com.grouprace.feature.profile.ui.main.components;

import android.view.View;
import android.widget.ImageButton;

import com.grouprace.feature.profile.R;

public class ProfileHeaderComponent {
    public interface Listener {
        void onBackClicked();
        void onSearchClicked();
        void onSettingsClicked();
    }

    private final ImageButton backButton;
    private final ImageButton searchButton;
    private final ImageButton settingButton;

    public ProfileHeaderComponent(View root) {
        backButton = root.findViewById(R.id.profile_back_button);
        searchButton = root.findViewById(R.id.profile_search_button);
        settingButton = root.findViewById(R.id.profile_setting_button);
    }

    public void bind(ProfileScreenConfig config, Listener listener) {
        backButton.setVisibility(config.isSelf() ? View.INVISIBLE : View.VISIBLE);
        settingButton.setVisibility(config.isSelf() ? View.VISIBLE : View.GONE);

        backButton.setOnClickListener(v -> listener.onBackClicked());
        searchButton.setOnClickListener(v -> listener.onSearchClicked());
        settingButton.setOnClickListener(v -> listener.onSettingsClicked());
    }
}
