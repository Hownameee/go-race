package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.grouprace.core.model.Record;

@Entity(tableName = "record")
public class RecordEntity {
    @PrimaryKey()
    public int recordId;
    public String activityType;
    public String startTime;
    public String endTime;
    public int ownerId;
    public int duration;
    public float distance;
    public float calories;
    public float heartRate;
    public float speed;
    public String imageUrl;

    public RecordEntity(int recordId, String activityType, String startTime, String endTime, int ownerId, int duration, float distance, float calories, float heartRate, float speed, String imageUrl) {
        this.recordId = recordId;
        this.ownerId = ownerId;
        this.activityType = activityType;
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
        return new Record(recordId, activityType, startTime, endTime, ownerId, duration, distance, calories, heartRate, speed, imageUrl);
    }
}
