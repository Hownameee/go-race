package com.grouprace.core.model.Profile;

public class ProfileOverview {
    private int userId;
    private String fullname;
    private String avatarUrl;
    private String bio;
    private String city;
    private String country;
    private int totalFollowings;
    private int totalFollowers;

    public ProfileOverview() {}

    public ProfileOverview(int userId, String fullname, String avatarUrl, String bio, String city,
                           String country, int totalFollowings, int totalFollowers) {
        this.userId = userId;
        this.fullname = fullname;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.city = city;
        this.country = country;
        this.totalFollowings = totalFollowings;
        this.totalFollowers = totalFollowers;
    }

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
