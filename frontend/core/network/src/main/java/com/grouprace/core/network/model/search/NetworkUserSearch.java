package com.grouprace.core.network.model.search;

import com.google.gson.annotations.SerializedName;
import com.grouprace.core.model.UserSearchResult;

public class NetworkUserSearch {

    @SerializedName(value = "user_id", alternate = {"club_id"})
    private int userId;

    @SerializedName(value = "fullname", alternate = {"name"})
    private String fullname;

    @SerializedName(value = "address", alternate = {"description"})
    private String address;

    @SerializedName(value = "avatar_url", alternate = {"avatar_s3_key"})
    private String avatarUrl;

    @SerializedName(value = "is_following", alternate = {"is_joined"})
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