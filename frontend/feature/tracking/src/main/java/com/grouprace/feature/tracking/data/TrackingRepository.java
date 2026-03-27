package com.grouprace.feature.tracking.data;

import com.grouprace.core.data.model.RoutePoint;

public interface TrackingRepository {

    void savePoint(RoutePoint point);
}
