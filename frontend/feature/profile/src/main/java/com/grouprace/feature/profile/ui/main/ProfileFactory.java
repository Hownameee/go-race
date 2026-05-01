package com.grouprace.feature.profile.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public final class ProfileFactory {
  private ProfileFactory() {}

  @NonNull
  public static Fragment create(@NonNull ProfileMode mode, int userId) {
    if (mode == ProfileMode.MY_PROFILE) {
      return MyProfileFragment.newInstance();
    }
    return UserProfileFragment.newInstance(userId);
  }

  @NonNull
  public static Fragment createMyProfile() {
    return create(ProfileMode.MY_PROFILE, -1);
  }

  @NonNull
  public static Fragment createUserProfile(int userId) {
    return create(ProfileMode.USER_PROFILE, userId);
  }
}
