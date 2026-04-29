package com.grouprace.feature.profile.ui.main.components;

public class ProfileScreenConfig {
    private final int userId;
    private final boolean self;

    public ProfileScreenConfig(int userId, boolean self) {
        this.userId = userId;
        this.self = self;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isSelf() {
        return self;
    }
}
