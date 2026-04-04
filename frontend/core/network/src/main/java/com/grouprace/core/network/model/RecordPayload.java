package com.grouprace.core.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecordPayload {

    @SerializedName("records")
    private List<NetworkRecord> records;

    public List<NetworkRecord> getRecords() {
        return records;
    }

    public void setRecords(List<NetworkRecord> records) {
        this.records = records;
    }
}
