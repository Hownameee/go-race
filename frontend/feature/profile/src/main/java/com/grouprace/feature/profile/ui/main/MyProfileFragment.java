package com.grouprace.feature.profile.ui.main;

import com.grouprace.feature.profile.ui.main.components.ProfileScreenConfig;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyProfileFragment extends ProfileFragment {

  public static MyProfileFragment newInstance() {
    return new MyProfileFragment();
  }

  @Override
  protected ProfileScreenConfig createInitialScreenConfig() {
    return new ProfileScreenConfig(-1, true);
  }

  @Override
  protected void handleHeaderBack() {
  }

  @Override
  protected void handleHeaderSettings() {
    navigator.openProfileSettings(this);
  }
}
