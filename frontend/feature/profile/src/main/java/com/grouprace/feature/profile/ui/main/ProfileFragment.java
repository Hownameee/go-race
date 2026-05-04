package com.grouprace.feature.profile.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.core.system.ui.TopAppBarConfig;
import com.grouprace.core.system.ui.TopAppBarHelper;
import com.grouprace.feature.profile.R;
import com.grouprace.feature.profile.ui.main.achievements_preview.ProfileAchievementsPreviewFragment;
import com.grouprace.feature.profile.ui.main.clubs_preview.ProfileClubsPreviewFragment;
import com.grouprace.feature.profile.ui.main.components.ProfileScreenConfig;
import com.grouprace.feature.profile.ui.main.links.ProfileLinksFragment;
import com.grouprace.feature.profile.ui.main.overview.ProfileOverviewFragment;
import com.grouprace.feature.profile.ui.main.stats.ProfileStatsFragment;
import com.grouprace.feature.profile.ui.main.streak.ProfileStreakFragment;

import javax.inject.Inject;

public abstract class ProfileFragment extends Fragment {

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
    setupTopBar(view);
    attachProfileSections(createInitialScreenConfig());
  }

  private void setupTopBar(View view) {
    ProfileScreenConfig config = createInitialScreenConfig();
    TopAppBarConfig.Builder builder = new TopAppBarConfig.Builder();

    if (config.isSelf()) {
      builder.setTitle("Profile");
      builder.addRightIcon(com.grouprace.core.system.R.drawable.ic_search, v -> navigator.navigateToSearch(this));
      builder.addRightIcon(R.drawable.ic_settings, v -> handleHeaderSettings());
    } else {
      builder.setTitle("User Profile");
      builder.setLeftIcon(com.grouprace.core.system.R.drawable.ic_back, v -> handleHeaderBack());
      builder.addRightIcon(com.grouprace.core.system.R.drawable.ic_search, v -> navigator.navigateToSearch(this));
    }

    TopAppBarHelper.setupTopAppBar(view, builder.build());
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


}