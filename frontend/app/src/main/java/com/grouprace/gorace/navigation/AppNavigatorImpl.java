package com.grouprace.gorace.navigation;

import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.grouprace.feature.club.ui.ClubsFragment;
import com.grouprace.gorace.R;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.club.ui.CreateClubFragment;
import com.grouprace.feature.notification.ui.NotificationFragment;
import com.grouprace.feature.posts.ui.MyPostsFragment;
import com.grouprace.feature.profile.ui.ChangeEmailFragment;
import com.grouprace.feature.profile.ui.ChangeEmailOtpFragment;
import com.grouprace.feature.profile.ui.ChangePasswordFragment;
import com.grouprace.feature.profile.ui.EditProfileFragment;
import com.grouprace.feature.profile.ui.PasswordResetOtpFragment;
import com.grouprace.feature.login.ui.LoginFragment;
import com.grouprace.feature.profile.ui.PasswordResetRequestFragment;
import com.grouprace.feature.profile.ui.ProfileComingSoonFragment;
import com.grouprace.feature.profile.ui.ProfileSettingsFragment;
import com.grouprace.feature.profile.ui.SetNewPasswordFragment;
import com.grouprace.feature.register.ui.RegisterFragment;
import com.grouprace.feature.search.ui.SearchFragment;
import com.grouprace.feature.posts.ui.AddPostFragment;
import com.grouprace.feature.posts.ui.VisualEditorFragment;
import com.grouprace.feature.club.ui.detail.tabs.CreateEventFragment;
import com.grouprace.feature.club.ui.detail.tabs.EventDetailFragment;
import com.grouprace.feature.club.ui.detail.tabs.OverviewFragment;
import com.grouprace.feature.club.ui.detail.tabs.ClubEventsFragment;
import com.grouprace.feature.club.ui.detail.tabs.ClubStatisticsFragment;
import com.grouprace.feature.club.ui.detail.tabs.EditClubFragment;
import com.grouprace.feature.club.ui.detail.ClubDetailFragment;

import javax.inject.Inject;

public class AppNavigatorImpl implements AppNavigator {

    @Inject
    public AppNavigatorImpl() {
    }

    // Top App Bar
    @Override
    public void navigateToNotification(Fragment currentFragment) {
        navigateTo(currentFragment, new NotificationFragment());
    }

    @Override
    public void navigateToSearch(Fragment currentFragment) {
        navigateTo(currentFragment, new SearchFragment());
    }

    // Profile
    @Override
    public void openForgotPassword(Fragment currentFragment) {
        navigateTo(currentFragment, PasswordResetRequestFragment.newInstance());
    }

    @Override
    public void openEditProfile(Fragment currentFragment) {
        navigateTo(currentFragment, new EditProfileFragment());
    }

    @Override
    public void openProfileSettings(Fragment currentFragment) {
        navigateTo(currentFragment, ProfileSettingsFragment.newInstance());
    }

    @Override
    public void openChangeEmail(Fragment currentFragment) {
        navigateTo(currentFragment, ChangeEmailFragment.newInstance());
    }

    @Override
    public void openChangeEmailOtp(Fragment currentFragment) {
        navigateTo(currentFragment, ChangeEmailOtpFragment.newInstance());
    }

    @Override
    public void openChangePassword(Fragment currentFragment) {
        navigateTo(currentFragment, ChangePasswordFragment.newInstance());
    }

    @Override
    public void openPasswordResetRequest(Fragment currentFragment) {
        navigateTo(currentFragment, PasswordResetRequestFragment.newInstance());
    }

    @Override
    public void openPasswordResetOtp(Fragment currentFragment) {
        navigateTo(currentFragment, PasswordResetOtpFragment.newInstance());
    }

    @Override
    public void openSetNewPassword(Fragment currentFragment) {
        navigateTo(currentFragment, SetNewPasswordFragment.newInstance());
    }

    @Override
    public void openComingSoon(Fragment currentFragment, String title) {
        navigateTo(currentFragment, ProfileComingSoonFragment.newInstance(title));
    }

    @Override
    public void openProfileComingSoon(Fragment currentFragment, String title) {
        navigateTo(currentFragment, ProfileComingSoonFragment.newInstance(title));
    }

    @Override
    public void openMyPosts(Fragment currentFragment) {
        navigateTo(currentFragment, MyPostsFragment.newInstance());
    }

    @Override
    public void openLogin(Fragment currentFragment) {
        navigateToRoot(currentFragment, LoginFragment.newInstance());
    }

    @Override
    public void openRegister(Fragment currentFragment) {
        navigateTo(currentFragment, RegisterFragment.newInstance());
    }

    @Override
    public void navigateToVisualEditor(Fragment currentFragment, String photoUri, String title, String distance, String time, String speed) {
        navigateTo(currentFragment, VisualEditorFragment.newInstance(photoUri, title, distance, time, speed));
    }

    @Override
    public void openAddPost(Fragment currentFragment, boolean withActivity, Integer clubId) {
        navigateTo(currentFragment, AddPostFragment.newInstance(withActivity, clubId));
    }

    @Override
    public void setBottomNavigationVisibility(Fragment fragment, boolean visible) {
        if (fragment != null && fragment.getActivity() != null) {
            View nav = fragment.getActivity().findViewById(R.id.bottom_navigation);
            if (nav != null) {
                nav.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }
    }
    @Override
    public void navigateToCreateClub(Fragment currentFragment) {
        navigateTo(currentFragment, new CreateClubFragment());
    }
    @Override
    public void navigateToClubs(Fragment currentFragment) {
        navigateTo(currentFragment, new ClubsFragment());
    }

    @Override
    public void openCreateEvent(Fragment currentFragment, int clubId) {
        navigateTo(currentFragment, CreateEventFragment.newInstance(clubId));
    }

    @Override
    public void openEventDetail(Fragment currentFragment, int clubId, int eventId) {
        navigateTo(currentFragment, EventDetailFragment.newInstance(clubId, eventId));
    }

    @Override
    public void openClubOverview(Fragment currentFragment, int clubId) {
        navigateTo(currentFragment, OverviewFragment.newInstance(clubId));
    }

    @Override
    public void openClubEvents(Fragment currentFragment, int clubId) {
        navigateTo(currentFragment, ClubEventsFragment.newInstance(clubId));
    }

    @Override
    public void openClubStats(Fragment currentFragment, int clubId) {
        navigateTo(currentFragment, ClubStatisticsFragment.newInstance(clubId));
    }

    @Override
    public void openEditClub(Fragment currentFragment, int clubId) {
        navigateTo(currentFragment, EditClubFragment.newInstance(clubId));
    }

    @Override
    public void openClubDetail(Fragment currentFragment, int clubId) {
        navigateTo(currentFragment, ClubDetailFragment.newInstance(clubId));
    }

    private void navigateTo(Fragment currentFragment, Fragment targetFragment) {
        if (currentFragment != null && currentFragment.getView() != null && currentFragment.getView().getParent() != null) {
            int containerId = ((ViewGroup) currentFragment.getView().getParent()).getId();
            currentFragment.requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(containerId, targetFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToRoot(Fragment currentFragment, Fragment targetFragment) {
        if (currentFragment != null && currentFragment.getView() != null && currentFragment.getView().getParent() != null) {
            int containerId = ((ViewGroup) currentFragment.getView().getParent()).getId();
            androidx.fragment.app.FragmentManager fm = currentFragment.requireActivity().getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            fm.beginTransaction()
                    .replace(containerId, targetFragment)
                    .commit();
        }
    }
}
