package com.grouprace.core.data.repository;

import com.grouprace.core.data.model.RoutePoint;

public interface TrackingRepository {

    void savePoint(RoutePoint point);
}
