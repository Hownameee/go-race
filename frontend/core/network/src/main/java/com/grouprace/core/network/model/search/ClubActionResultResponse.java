package com.grouprace.core.network.model.search;

import com.google.gson.annotations.SerializedName;

public class ClubActionResultResponse {
    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
