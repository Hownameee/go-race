package com.grouprace.core.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PostPayload {

    @SerializedName("posts")
    private List<NetworkPost> posts;

    @SerializedName("nextCursor")
    private String nextCursor;

    public List<NetworkPost> getPosts() {
        return posts;
    }

    public void setPosts(List<NetworkPost> posts) {
        this.posts = posts;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
}
