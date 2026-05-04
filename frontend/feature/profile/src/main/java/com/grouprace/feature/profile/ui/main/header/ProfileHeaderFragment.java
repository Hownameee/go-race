package com.grouprace.feature.profile.ui.main.header;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grouprace.feature.profile.R;
import com.grouprace.feature.profile.ui.main.components.ProfileHeaderComponent;
import com.grouprace.feature.profile.ui.main.components.ProfileScreenConfig;

public class ProfileHeaderFragment extends Fragment {
  private static final String ARG_USER_ID = "arg_user_id";
  private static final String ARG_IS_SELF = "arg_is_self";

  public ProfileHeaderFragment() {
    super(R.layout.fragment_profile_header);
  }

  public static ProfileHeaderFragment newInstance(int userId, boolean isSelf) {
    ProfileHeaderFragment fragment = new ProfileHeaderFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_USER_ID, userId);
    args.putBoolean(ARG_IS_SELF, isSelf);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Fragment parentFragment = requireParentFragment();
    if (!(parentFragment instanceof ProfileHeaderHost)) {
      throw new IllegalStateException("Parent fragment must implement ProfileHeaderHost");
    }

    int userId = getArguments() != null ? getArguments().getInt(ARG_USER_ID, -1) : -1;
    boolean isSelf = getArguments() == null || getArguments().getBoolean(ARG_IS_SELF, true);
    ProfileHeaderHost host = (ProfileHeaderHost) parentFragment;
    ProfileHeaderComponent headerComponent = new ProfileHeaderComponent(view);
    headerComponent.bind(new ProfileScreenConfig(userId, isSelf), new ProfileHeaderComponent.Listener() {
      @Override
      public void onBackClicked() {
        host.onProfileHeaderBackClicked();
      }

      @Override
      public void onSearchClicked() {
        host.onProfileHeaderSearchClicked();
      }

      @Override
      public void onSettingsClicked() {
        host.onProfileHeaderSettingsClicked();
      }
    });
  }
}
