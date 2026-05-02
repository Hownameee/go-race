package com.grouprace.core.navigation;

import androidx.fragment.app.Fragment;
import com.grouprace.core.model.PlannedRoute;

public interface AppNavigator {
    void navigateToNotification(Fragment currentFragment);
    void navigateToSearch(Fragment currentFragment);
    void openForgotPassword(Fragment currentFragment);
    void openEditProfile(Fragment currentFragment);
    void openProfileSettings(Fragment currentFragment);
    void openChangeEmail(Fragment currentFragment);
    void openChangeEmailOtp(Fragment currentFragment);
    void openChangePassword(Fragment currentFragment);
//    void openPasswordResetRequest(Fragment currentFragment);
    void openPasswordResetOtp(Fragment currentFragment);
    void openSetNewPassword(Fragment currentFragment);
    void openComingSoon(Fragment currentFragment, String title);
    void openProfileComingSoon(Fragment currentFragment, String title);
    void openMyPosts(Fragment currentFragment);
    // profile section
    void openProfileActivities(Fragment currentFragment, int userId, String profileName, boolean isSelf);
    void openProfilePosts(Fragment currentFragment, int userId, String profileName, boolean isSelf);
    void openProfileRoutes(Fragment currentFragment, int userId, String profileName, boolean isSelf);
    void openProfileClubs(Fragment currentFragment, int userId, String profileName, boolean isSelf);
    void openProfileFollowList(Fragment currentFragment, int userId, String profileName, boolean isSelf, String initialTab);
    void openProfileStatistics(Fragment currentFragment, int userId, boolean isSelf);
    void openProfileAchievements(Fragment currentFragment, int userId, boolean isSelf);
    void openUserProfile(Fragment currentFragment, int userId);
    void openLogin(Fragment currentFragment);
    void openRegister(Fragment currentFragment);
    void openRegister(Fragment currentFragment, String fullname, String email);
    void openRegister(Fragment currentFragment, String fullname, String email, String googleIdToken);
    void navigateToVisualEditor(Fragment currentFragment, String photoUri, String title, String distance, String time, String speed);
    void openAddPost(Fragment currentFragment, boolean withActivity, Integer clubId);
    void setBottomNavigationVisibility(Fragment fragment, boolean visible);
    void navigateToCreateClub(Fragment currentFragment);
    void navigateToClubs(Fragment currentFragment);
    void openCreateEvent(Fragment currentFragment, int clubId);
    void openEventDetail(Fragment currentFragment, int clubId, int eventId);
    void openClubOverview(Fragment currentFragment, int clubId);
    void openClubEvents(Fragment currentFragment, int clubId);
    void openClubStats(Fragment currentFragment, int clubId);
    void openEditClub(Fragment currentFragment, int clubId);
    void openClubDetail(Fragment currentFragment, int clubId);
    void navigateToRunWithRoute(Fragment currentFragment, PlannedRoute route);
}
