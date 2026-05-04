package com.grouprace.feature.profile.ui.main.overview;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Profile.ProfileOverview;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.profile.R;
import com.grouprace.feature.profile.ui.follow.FollowListFragment;
import com.grouprace.feature.profile.ui.main.components.ProfileOverviewComponent;
import com.grouprace.feature.profile.ui.main.components.ProfileScreenConfig;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileOverviewFragment extends Fragment {
  private static final String ARG_USER_ID = "arg_user_id";
  private static final String ARG_IS_SELF = "arg_is_self";

  @Inject
  AppNavigator navigator;

  private ProfileOverviewViewModel viewModel;
  private ProfileOverviewComponent component;
  private ProfileOverview currentOverview;
  private int userId;
  private boolean self;

  public ProfileOverviewFragment() {
    super(R.layout.fragment_profile_overview);
  }

  public static ProfileOverviewFragment newInstance(int userId, boolean isSelf) {
    ProfileOverviewFragment fragment = new ProfileOverviewFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_USER_ID, userId);
    args.putBoolean(ARG_IS_SELF, isSelf);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    userId = getArguments() != null ? getArguments().getInt(ARG_USER_ID, -1) : -1;
    self = getArguments() == null || getArguments().getBoolean(ARG_IS_SELF, true);
    viewModel = new ViewModelProvider(this).get(ProfileOverviewViewModel.class);
    viewModel.initialize(userId, self);

    component = new ProfileOverviewComponent(this, view);
    component.bindConfig(new ProfileScreenConfig(userId, self), new ProfileOverviewComponent.Listener() {
      @Override
      public void onFollowingClicked() {
        openFollowList(FollowListFragment.TAB_FOLLOWING);
      }

      @Override
      public void onFollowersClicked() {
        openFollowList(FollowListFragment.TAB_FOLLOWERS);
      }

      @Override
      public void onPrimaryActionClicked() {
        if (self) {
          navigator.openEditProfile(ProfileOverviewFragment.this);
        } else {
          toggleFollow();
        }
      }
    });

    observeOverview();
    viewModel.loadOverview();
  }

  private void observeOverview() {
    viewModel.getOverview().observe(getViewLifecycleOwner(), result -> {
      if (result instanceof Result.Success) {
        currentOverview = ((Result.Success<ProfileOverview>) result).data;
        component.bind(currentOverview);
      } else if (result instanceof Result.Error) {
        String message = ((Result.Error<ProfileOverview>) result).message;
        Toast.makeText(requireContext(), message != null ? message : "Failed to load profile overview.", Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void openFollowList(String initialTab) {
    if (currentOverview == null) {
      return;
    }
    navigator.openProfileFollowList(this, currentOverview.getUserId(), currentOverview.getFullname(), self, initialTab);
  }

  private void toggleFollow() {
    if (currentOverview == null) {
      return;
    }

    component.setPrimaryActionEnabled(false);
    LiveData<Result<Boolean>> action = currentOverview.isFollowing()
        ? viewModel.unfollowUser()
        : viewModel.followUser();

    action.observe(getViewLifecycleOwner(), result -> {
      if (result instanceof Result.Success) {
        viewModel.applyFollowingState(!currentOverview.isFollowing());
        component.setPrimaryActionEnabled(true);
      } else if (result instanceof Result.Error) {
        component.setPrimaryActionEnabled(true);
        String message = ((Result.Error<Boolean>) result).message;
        Toast.makeText(requireContext(), message != null ? message : "Follow action failed.", Toast.LENGTH_SHORT).show();
      }
    });
  }
}
