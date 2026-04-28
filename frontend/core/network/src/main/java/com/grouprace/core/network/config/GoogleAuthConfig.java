package com.grouprace.core.network.config;

import com.grouprace.core.network.BuildConfig;

public final class GoogleAuthConfig {
  public static final String WEB_CLIENT_ID = BuildConfig.GOOGLE_WEB_CLIENT_ID;

  private GoogleAuthConfig() {
  }

  public static boolean isConfigured() {
    return WEB_CLIENT_ID != null && !WEB_CLIENT_ID.trim().isEmpty();
  }
}
