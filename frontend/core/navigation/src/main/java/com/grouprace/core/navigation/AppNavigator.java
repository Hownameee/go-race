package com.grouprace.core.navigation;

import androidx.fragment.app.Fragment;

public interface AppNavigator {
    void navigateToNotification(Fragment currentFragment);
    void navigateToSearch(Fragment currentFragment);
    void openForgotPassword(Fragment currentFragment);
    void openEditProfile(Fragment currentFragment);
    void openProfileSettings(Fragment currentFragment);
    void openChangeEmail(Fragment currentFragment);
    void openChangeEmailOtp(Fragment currentFragment);
    void openChangePassword(Fragment currentFragment);
    void openPasswordResetRequest(Fragment currentFragment);
    void openPasswordResetOtp(Fragment currentFragment);
    void openSetNewPassword(Fragment currentFragment);
    void openComingSoon(Fragment currentFragment, String title);
    void openProfileComingSoon(Fragment currentFragment, String title);
    void openMyPosts(Fragment currentFragment);
    void openLogin(Fragment currentFragment);
    void openRegister(Fragment currentFragment);
    void navigateToVisualEditor(Fragment currentFragment, String photoUri, String title, String distance, String time, String speed);
    void setBottomNavigationVisibility(Fragment fragment, boolean visible);
}
