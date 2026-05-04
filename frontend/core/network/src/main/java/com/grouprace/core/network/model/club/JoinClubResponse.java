package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;

public class JoinClubResponse {
    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
