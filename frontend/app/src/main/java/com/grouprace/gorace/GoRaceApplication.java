package com.grouprace.gorace;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.work.Configuration;
import androidx.hilt.work.HiltWorkerFactory;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class GoRaceApplication extends Application implements Configuration.Provider {

    @Inject
    HiltWorkerFactory workerFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        // App is dark-only — force night mode so all system dialogs match
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build();
    }
}
