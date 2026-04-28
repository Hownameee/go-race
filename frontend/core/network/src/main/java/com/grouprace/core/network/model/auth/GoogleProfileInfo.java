package com.grouprace.core.network.model.auth;

import com.google.gson.annotations.SerializedName;

public class GoogleProfileInfo {
  @SerializedName("email")
  private String email;

  @SerializedName("fullname")
  private String fullname;

  @SerializedName("avatar_url")
  private String avatarUrl;

  public String getEmail() {
    return email;
  }

  public String getFullname() {
    return fullname;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }
}
