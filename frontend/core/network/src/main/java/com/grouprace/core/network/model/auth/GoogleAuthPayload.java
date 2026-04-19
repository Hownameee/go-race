package com.grouprace.core.network.model.auth;

import com.google.gson.annotations.SerializedName;

public class GoogleAuthPayload {
  @SerializedName("id_token")
  private final String idToken;

  @SerializedName("username")
  private final String username;

  @SerializedName("birthdate")
  private final String birthdate;

  public GoogleAuthPayload(String idToken) {
    this(idToken, null, null);
  }

  public GoogleAuthPayload(String idToken, String username, String birthdate) {
    this.idToken = idToken;
    this.username = username;
    this.birthdate = birthdate;
  }

  public String getIdToken() {
    return idToken;
  }

  public String getUsername() {
    return username;
  }

  public String getBirthdate() {
    return birthdate;
  }
}
