package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activities")
public class Activity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String title;
    public long startTime;
    public long endTime;
    public double distanceKm;
    public long elapsedTimeMs;
    public double paceMinPerKm;
    public boolean completed;

    public Activity() {}
}
