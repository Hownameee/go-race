package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.grouprace.core.model.Record;

@Entity(tableName = "record")
public class RecordEntity {
    @PrimaryKey
    public int recordId;
    public String activityType;
    public String title;
    public String startTime;
    public String endTime;
    public int ownerId;
    public int duration;      // seconds
    public double distance;   // km
    public float calories;
    public float heartRate;
    public double speed;      // km/h
    public String imageUrl;

    public RecordEntity(int recordId, String activityType, String title, String startTime, String endTime, int ownerId, int duration, double distance, float calories, float heartRate, double speed, String imageUrl) {
        this.recordId = recordId;
        this.ownerId = ownerId;
        this.activityType = activityType;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.distance = distance;
        this.calories = calories;
        this.heartRate = heartRate;
        this.speed = speed;
        this.imageUrl = imageUrl;
    }

    public Record asExternalModel() {
        return new Record(recordId, activityType, title, startTime, endTime, ownerId, duration, distance, calories, heartRate, speed, imageUrl);
    }
}
