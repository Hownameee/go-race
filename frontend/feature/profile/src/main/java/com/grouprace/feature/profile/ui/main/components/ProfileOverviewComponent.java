package com.grouprace.feature.profile.ui.main.components;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grouprace.core.model.Profile.ProfileOverview;
import com.grouprace.core.system.ui.ViewHelper;
import com.grouprace.feature.profile.R;

public class ProfileOverviewComponent {
  public interface Listener {
    void onFollowingClicked();

    void onFollowersClicked();

    void onPrimaryActionClicked();
  }

  private final Fragment fragment;
  private final ImageView avatar;
  private final TextView fullname;
  private final TextView bio;
  private final TextView city;
  private final TextView country;
  private final TextView totalFollowings;
  private final TextView totalFollowers;
  private final View followingLink;
  private final View followersLink;
  private final Button primaryButton;
  private ProfileScreenConfig config;

  public ProfileOverviewComponent(Fragment fragment, View root) {
    this.fragment = fragment;
    avatar = root.findViewById(R.id.avatar);
    fullname = root.findViewById(R.id.profile_fullname);
    bio = root.findViewById(R.id.profile_bio);
    city = root.findViewById(R.id.profile_city);
    country = root.findViewById(R.id.profile_country);
    totalFollowings = root.findViewById(R.id.profile_total_followings);
    totalFollowers = root.findViewById(R.id.profile_total_followers);
    followingLink = root.findViewById(R.id.profile_following_link);
    followersLink = root.findViewById(R.id.profile_followers_link);
    primaryButton = root.findViewById(R.id.profile_edit_button);
  }

  public void bindConfig(ProfileScreenConfig config, Listener listener) {
    this.config = config;
    followingLink.setOnClickListener(v -> listener.onFollowingClicked());
    followersLink.setOnClickListener(v -> listener.onFollowersClicked());
    primaryButton.setOnClickListener(v -> listener.onPrimaryActionClicked());
  }

  public void bind(@Nullable ProfileOverview overview) {
    if (overview == null) {
      return;
    }

    ViewHelper.bindOptionalText(fullname, overview.getFullname());
    ViewHelper.bindOptionalText(bio, overview.getBio());
    ViewHelper.bindOptionalText(city, overview.getCity());
    ViewHelper.bindOptionalText(country, overview.getCountry());
    totalFollowings.setText(String.valueOf(overview.getTotalFollowings()));
    totalFollowers.setText(String.valueOf(overview.getTotalFollowers()));
    ProfileAvatarLoader.load(fragment, avatar, overview.getAvatarUrl());

    if (config != null && config.isSelf()) {
      primaryButton.setText("Edit");
      primaryButton.setBackgroundResource(com.grouprace.core.system.R.drawable.bg_button_rounded);
      primaryButton.setTextColor(fragment.requireContext().getColor(android.R.color.black));
    } else {
      bindFollowingState(overview.isFollowing());
    }
  }

  public void bindFollowingState(boolean following) {
    primaryButton.setText(following ? "Following" : "Follow");
    primaryButton.setBackgroundResource(following
        ? com.grouprace.core.system.R.drawable.bg_button_secondary_rounded
        : com.grouprace.core.system.R.drawable.bg_button_rounded);
    primaryButton.setTextColor(fragment.requireContext().getColor(android.R.color.black));
  }

  public void setPrimaryActionEnabled(boolean enabled) {
    primaryButton.setEnabled(enabled);
  }
}
