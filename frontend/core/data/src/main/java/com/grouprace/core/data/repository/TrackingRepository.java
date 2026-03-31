package com.grouprace.core.data.repository;

import com.grouprace.core.data.model.Activity;
import com.grouprace.core.data.model.RoutePoint;

import java.util.List;

public interface TrackingRepository {

    void savePoint(RoutePoint point);

    long createActivity(Activity activity);

    void updateActivity(Activity activity);

    Activity getActivityById(long id);

    List<RoutePoint> getPointsForActivity(long activityId);
}
