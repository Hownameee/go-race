package com.grouprace.gorace.navigation;

import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.grouprace.core.navigation.AppNavigator;
import com.grouprace.feature.notification.ui.NotificationFragment;
import com.grouprace.feature.search.ui.SearchFragment;

import javax.inject.Inject;

public class AppNavigatorImpl implements AppNavigator {

    @Inject
    public AppNavigatorImpl() {
    }

    @Override
    public void navigateToNotification(Fragment currentFragment) {
        if (currentFragment.getView() != null && currentFragment.getView().getParent() != null) {
            int containerId = ((ViewGroup) currentFragment.getView().getParent()).getId();
            currentFragment.requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(containerId, new NotificationFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void navigateToSearch(Fragment currentFragment) {
        if (currentFragment.getView() != null && currentFragment.getView().getParent() != null) {
            int containerId = ((ViewGroup) currentFragment.getView().getParent()).getId();
            currentFragment.requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(containerId, new SearchFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
