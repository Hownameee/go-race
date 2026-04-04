package com.grouprace.core.network.model.search;

import com.google.gson.annotations.SerializedName;
import com.grouprace.core.model.UserSearchResult;

public class NetworkUserSearch {

    @SerializedName("user_id")
    private int userId;

    @SerializedName("fullname")
    private String fullname;

    @SerializedName("address")
    private String address;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("is_following")
    private int isFollowing;

    public NetworkUserSearch() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public boolean isFollowing() { return (isFollowing == 1); }
    public void setFollowing(boolean following) { this.isFollowing = following ? 1 : 0; }

    // Hàm chuyển đổi sang Model của UI (External Model)
    public UserSearchResult asExternalModel() {
        return new UserSearchResult(
                userId,
                fullname,
                address,
                avatarUrl,
                isFollowing()
        );
    }
}