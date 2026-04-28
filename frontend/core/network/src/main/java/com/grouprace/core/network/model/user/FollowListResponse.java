package com.grouprace.core.network.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FollowListResponse {
    @SerializedName("followers")
    private List<FollowUserResponse> followers;

    @SerializedName("following")
    private List<FollowUserResponse> following;

    @SerializedName("nextCursor")
    private String nextCursor;

    public List<FollowUserResponse> getFollowers() {
        return followers;
    }

    public List<FollowUserResponse> getFollowing() {
        return following;
    }

    public String getNextCursor() {
        return nextCursor;
    }
}
