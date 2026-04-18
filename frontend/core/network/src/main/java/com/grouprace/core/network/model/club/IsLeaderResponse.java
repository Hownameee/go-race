package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;

public class IsLeaderResponse {
    @SerializedName("is_leader")
    private boolean isLeader;

    public boolean isLeader() { return isLeader; }
}
