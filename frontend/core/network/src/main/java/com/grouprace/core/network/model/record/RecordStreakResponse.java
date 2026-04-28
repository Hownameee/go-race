package com.grouprace.core.network.model.record;

import com.google.gson.annotations.SerializedName;

public class RecordStreakResponse {
    @SerializedName("current_streak_days")
    private int currentStreakDays;

    @SerializedName("longest_streak_days")
    private int longestStreakDays;

    @SerializedName("total_active_days")
    private int totalActiveDays;

    @SerializedName("last_record_date")
    private String lastRecordDate;

    @SerializedName("today_has_record")
    private boolean todayHasRecord;

    public int getCurrentStreakDays() {
        return currentStreakDays;
    }

    public int getLongestStreakDays() {
        return longestStreakDays;
    }

    public int getTotalActiveDays() {
        return totalActiveDays;
    }

    public String getLastRecordDate() {
        return lastRecordDate;
    }

    public boolean isTodayHasRecord() {
        return todayHasRecord;
    }
}
