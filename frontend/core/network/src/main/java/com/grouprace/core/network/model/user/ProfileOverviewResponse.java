package com.grouprace.core.network.model.user;

import com.google.gson.annotations.SerializedName;

public class ProfileOverviewResponse {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("fullname")
    private String fullname;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("bio")
    private String bio;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    @SerializedName("total_followings")
    private int totalFollowings;

    @SerializedName("total_followers")
    private int totalFollowers;

    public ProfileOverviewResponse() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public int getTotalFollowings() { return totalFollowings; }
    public void setTotalFollowings(int totalFollowings) { this.totalFollowings = totalFollowings; }

    public int getTotalFollowers() { return totalFollowers; }
    public void setTotalFollowers(int totalFollowers) { this.totalFollowers = totalFollowers; }
}
