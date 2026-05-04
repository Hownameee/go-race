package com.grouprace.feature.profile.ui.follow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.UserRepository;
import com.grouprace.core.model.Profile.FollowUser;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FollowListViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Result<List<FollowUser>>> followUsers = new MutableLiveData<>();
    private final MutableLiveData<String> selectedTab = new MutableLiveData<>(FollowListFragment.TAB_FOLLOWERS);
    private LiveData<Result<List<FollowUser>>> currentSource;
    private Observer<Result<List<FollowUser>>> currentObserver;
    private int userId = -1;

    @Inject
    public FollowListViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void initialize(int userId) {
        this.userId = userId;
    }

    public LiveData<Result<List<FollowUser>>> getFollowUsers() {
        return followUsers;
    }

    public LiveData<String> getSelectedTab() {
        return selectedTab;
    }

    public void selectTab(String tab) {
        if (tab == null || userId <= 0) {
            return;
        }
        selectedTab.setValue(tab);
        loadSelectedTab();
    }

    public void loadSelectedTab() {
        if (userId <= 0) {
            return;
        }

        if (currentSource != null && currentObserver != null) {
            currentSource.removeObserver(currentObserver);
        }

        String activeTab = selectedTab.getValue();
        currentSource = FollowListFragment.TAB_FOLLOWING.equals(activeTab)
                ? userRepository.getFollowing(userId)
                : userRepository.getFollowers(userId);
        currentObserver = result -> followUsers.setValue(result);
        currentSource.observeForever(currentObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentSource != null && currentObserver != null) {
            currentSource.removeObserver(currentObserver);
        }
    }
}
