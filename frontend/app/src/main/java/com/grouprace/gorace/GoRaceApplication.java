package com.grouprace.gorace;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class GoRaceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // App is dark-only — force night mode so all system dialogs match
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
