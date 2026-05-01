package com.grouprace.feature.profile.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.profile.R;
import com.grouprace.feature.profile.ui.main.achievements_preview.ProfileAchievementsPreviewFragment;
import com.grouprace.feature.profile.ui.main.clubs_preview.ProfileClubsPreviewFragment;
import com.grouprace.feature.profile.ui.main.components.ProfileScreenConfig;
import com.grouprace.feature.profile.ui.main.header.ProfileHeaderFragment;
import com.grouprace.feature.profile.ui.main.header.ProfileHeaderHost;
import com.grouprace.feature.profile.ui.main.links.ProfileLinksFragment;
import com.grouprace.feature.profile.ui.main.overview.ProfileOverviewFragment;
import com.grouprace.feature.profile.ui.main.stats.ProfileStatsFragment;
import com.grouprace.feature.profile.ui.main.streak.ProfileStreakFragment;

import javax.inject.Inject;

public abstract class ProfileFragment extends Fragment implements ProfileHeaderHost {

  @Inject
  protected AppNavigator navigator;

  protected ProfileFragment() {
    super(R.layout.fragment_profile);
  }

  protected abstract ProfileScreenConfig createInitialScreenConfig();

  protected abstract void handleHeaderBack();

  protected abstract void handleHeaderSettings();

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    attachProfileSections(createInitialScreenConfig());
  }

  /**
   * Like playing with lego, attach child fragments into Profile fragment
   * Here, child fragments act as black boxes to Profile fragment
   * */
  private void attachProfileSections(ProfileScreenConfig config) {
    int userId = config.getUserId();
    boolean self = config.isSelf();


    getChildFragmentManager().beginTransaction()
      .replace(
        R.id.profile_header_fragment_container,
        ProfileHeaderFragment.newInstance(userId, self)
      )
      .replace(
        R.id.profile_overview_fragment_container,
        ProfileOverviewFragment.newInstance(userId, self)
      )
      .replace(
        R.id.profile_stats_fragment_container,
        ProfileStatsFragment.newInstance(userId, self)
      )
      .replace(
        R.id.profile_streak_fragment_container,
        ProfileStreakFragment.newInstance(userId, self)
      )
      .replace(
        R.id.profile_links_fragment_container,
        ProfileLinksFragment.newInstance(userId, null, self)
      )
      .replace(
        R.id.profile_achievements_preview_fragment_container,
        ProfileAchievementsPreviewFragment.newInstance(userId, self)
      )
      .replace(
        R.id.profile_clubs_preview_fragment_container,
        ProfileClubsPreviewFragment.newInstance(userId, null, self)
      )
      .commit();
  }

  @Override
  public void onProfileHeaderBackClicked() {
    handleHeaderBack();
  }

  @Override
  public void onProfileHeaderSearchClicked() {
    navigator.navigateToSearch(this);
  }

  @Override
  public void onProfileHeaderSettingsClicked() {
    handleHeaderSettings();
  }
}
