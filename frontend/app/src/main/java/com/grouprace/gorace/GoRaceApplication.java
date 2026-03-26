package com.grouprace.gorace;

import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class GoRaceApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppForegroundTracker());
    }
}
