package com.grouprace.core.network.model.auth;

import com.google.gson.annotations.SerializedName;

public class GoogleAuthResponse {
  @SerializedName("token")
  private String token;

  @SerializedName("requires_profile_completion")
  private boolean requiresProfileCompletion;

  @SerializedName("profile")
  private GoogleProfileInfo profile;

  public String getToken() {
    return token;
  }

  public boolean isRequiresProfileCompletion() {
    return requiresProfileCompletion;
  }

  public GoogleProfileInfo getProfile() {
    return profile;
  }
}
