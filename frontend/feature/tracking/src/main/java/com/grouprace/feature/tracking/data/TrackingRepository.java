package com.grouprace.feature.tracking.data;

import android.location.Location;

import androidx.lifecycle.LiveData;

import com.grouprace.core.data.model.RoutePoint;

public interface TrackingRepository {

    void startNewActivity();

    void savePoint(RoutePoint point);

    LiveData<Location> getCurrentLocation();

    void stopActivity();
}