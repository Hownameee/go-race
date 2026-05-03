package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.grouprace.core.model.Profile.ProfileOverview;

@Entity(tableName = "profile_overviews")
public class ProfileOverviewEntity {
  @PrimaryKey
  public int userId;
  public boolean selfProfile;
  public String fullname;
  public String avatarUrl;
  public String bio;
  public String city;
  public String country;
  public int totalFollowings;
  public int totalFollowers;
  public boolean isFollowing;

  public ProfileOverviewEntity(int userId, boolean selfProfile, String fullname, String avatarUrl,
                               String bio, String city, String country, int totalFollowings,
                               int totalFollowers, boolean isFollowing) {
    this.userId = userId;
    this.selfProfile = selfProfile;
    this.fullname = fullname;
    this.avatarUrl = avatarUrl;
    this.bio = bio;
    this.city = city;
    this.country = country;
    this.totalFollowings = totalFollowings;
    this.totalFollowers = totalFollowers;
    this.isFollowing = isFollowing;
  }

  public ProfileOverview asExternalModel() {
    return new ProfileOverview(
        userId,
        fullname,
        avatarUrl,
        bio,
        city,
        country,
        totalFollowings,
        totalFollowers,
        isFollowing
    );
  }
}
