package com.grouprace.core.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CommentPayload {

    @SerializedName("comments")
    private List<NetworkComment> comments;

    @SerializedName("nextCursor")
    private String nextCursor;

    public List<NetworkComment> getComments() {
        return comments;
    }

    public void setComments(List<NetworkComment> comments) {
        this.comments = comments;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
}
