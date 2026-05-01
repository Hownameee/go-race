package com.grouprace.feature.profile.ui.main;

import android.os.Bundle;

import com.grouprace.feature.profile.ui.main.components.ProfileScreenConfig;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserProfileFragment extends ProfileFragment {
  private static final String ARG_USER_ID = "arg_user_id";

  public static UserProfileFragment newInstance(int userId) {
    UserProfileFragment fragment = new UserProfileFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_USER_ID, userId);
    fragment.setArguments(args);
    return fragment;
  }

    @Override
    protected ProfileScreenConfig createInitialScreenConfig() {
        Bundle args = getArguments();
        if (args == null || !args.containsKey(ARG_USER_ID)) {
            throw new IllegalStateException("UserProfileFragment requires ARG_USER_ID");
        }

        // userId = -1 is for MyProfileFragment
        int userId = args.getInt(ARG_USER_ID, -1);
        if (userId <= 0) {
            throw new IllegalStateException("Invalid user id for UserProfileFragment: " + userId);
        }

        return new ProfileScreenConfig(userId, false);
    }

  @Override
  protected void handleHeaderBack() {
    requireActivity().getOnBackPressedDispatcher().onBackPressed();
  }

  @Override
  protected void handleHeaderSettings() {
  }
}
