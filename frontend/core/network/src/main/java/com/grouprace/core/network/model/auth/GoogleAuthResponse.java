package com.grouprace.core.network.model.auth;

import com.google.gson.annotations.SerializedName;

public class GoogleAuthResponse {
  @SerializedName("access_token")
  private String accessToken;

  @SerializedName("refresh_token")
  private String refreshToken;

  @SerializedName("requires_profile_completion")
  private boolean requiresProfileCompletion;

  @SerializedName("profile")
  private GoogleProfileInfo profile;

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public boolean isRequiresProfileCompletion() {
    return requiresProfileCompletion;
  }

  public GoogleProfileInfo getProfile() {
    return profile;
  }
}
