package com.grouprace.gorace.navigation;

import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.grouprace.gorace.R;
import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.notification.ui.NotificationFragment;
import com.grouprace.feature.posts.ui.MyPostsFragment;
import com.grouprace.feature.profile.ui.edit.EditProfileFragment;
import com.grouprace.feature.profile.ui.follow.FollowListFragment;
import com.grouprace.feature.profile.ui.main.achievements.ProfileAchievementsFragment;
import com.grouprace.feature.profile.ui.main.activities.ProfileRecordsFragment;
import com.grouprace.feature.profile.ui.main.ProfileComingSoonFragment;
import com.grouprace.feature.profile.ui.main.UserProfileFragment;
import com.grouprace.feature.profile.ui.main.statistics.ProfileStatisticsDetailFragment;
import com.grouprace.feature.login.ui.LoginFragment;
import com.grouprace.feature.profile.ui.settings.ProfileSettingsFragment;
import com.grouprace.feature.profile.ui.settings.email.ChangeEmailFragment;
import com.grouprace.feature.profile.ui.settings.email.ChangeEmailOtpFragment;
import com.grouprace.feature.profile.ui.settings.password.ChangePasswordFragment;
import com.grouprace.feature.profile.ui.settings.password.PasswordResetOtpFragment;
import com.grouprace.feature.profile.ui.settings.password.PasswordResetRequestFragment;
import com.grouprace.feature.profile.ui.settings.password.SetNewPasswordFragment;
import com.grouprace.feature.register.ui.RegisterFragment;
import com.grouprace.feature.search.ui.SearchFragment;
import com.grouprace.feature.posts.ui.AddPostFragment;
import com.grouprace.feature.posts.ui.VisualEditorFragment;

import javax.inject.Inject;

public class AppNavigatorImpl implements AppNavigator {

    @Inject
    public AppNavigatorImpl() {
    }

    // ===== Top App Bar =====
    @Override
    public void navigateToNotification(Fragment currentFragment) {
        navigateTo(currentFragment, new NotificationFragment());
    }

    @Override
    public void navigateToSearch(Fragment currentFragment) {
        navigateTo(currentFragment, new SearchFragment());
    }

    // ===== Profile =====
    @Override
    public void openEditProfile(Fragment currentFragment) {
      navigateTo(currentFragment, new EditProfileFragment());
    }

    @Override
    public void openProfileComingSoon(Fragment currentFragment, String title) {
      navigateTo(currentFragment, ProfileComingSoonFragment.newInstance(title));
    }

    @Override
    public void openMyPosts(Fragment currentFragment) {
      navigateTo(currentFragment, MyPostsFragment.newInstance());
    }

//  public void openMyRoutes(Fragment currentFragment)

    @Override
    public void openProfileActivities(Fragment currentFragment, int userId, String profileName, boolean isSelf) {
      navigateTo(currentFragment, ProfileRecordsFragment.newInstance(userId, profileName, isSelf));
    }

    @Override
    public void openProfileFollowList(Fragment currentFragment, int userId, String profileName, boolean isSelf, String initialTab) {
      navigateTo(currentFragment, FollowListFragment.newInstance(userId, profileName, isSelf, initialTab));
    }

    @Override
    public void openProfileStatistics(Fragment currentFragment, int userId, boolean isSelf) {
      navigateTo(currentFragment, ProfileStatisticsDetailFragment.newInstance(isSelf, userId));
    }

    @Override
    public void openProfileAchievements(Fragment currentFragment, int userId, boolean isSelf) {
      navigateTo(currentFragment, ProfileAchievementsFragment.newInstance(isSelf, userId));
    }

    @Override
    public void openUserProfile(Fragment currentFragment, int userId) {
      navigateTo(currentFragment, UserProfileFragment.newInstance(userId));
    }

    // Profile setting
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

    //    @Override
    //    public void openPasswordResetRequest(Fragment currentFragment) {
    //      navigateTo(currentFragment, PasswordResetRequestFragment.newInstance());
    //    }

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

    // ===== Authentication =====
    @Override
    public void openForgotPassword(Fragment currentFragment) {
        navigateTo(currentFragment, PasswordResetRequestFragment.newInstance());
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
    public void openRegister(Fragment currentFragment, String fullname, String email) {
        navigateTo(currentFragment, RegisterFragment.newInstance(fullname, email));
    }

    @Override
    public void openRegister(Fragment currentFragment, String fullname, String email, String googleIdToken) {
        navigateTo(currentFragment, RegisterFragment.newInstance(fullname, email, googleIdToken));
    }

    // ===== Others =====
    @Override
    public void navigateToVisualEditor(Fragment currentFragment, String photoUri, String title, String distance, String time, String speed) {
        navigateTo(currentFragment, VisualEditorFragment.newInstance(photoUri, title, distance, time, speed));
    }

    @Override
    public void openAddPost(Fragment currentFragment, boolean withActivity) {
        navigateTo(currentFragment, AddPostFragment.newInstance(withActivity));
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
