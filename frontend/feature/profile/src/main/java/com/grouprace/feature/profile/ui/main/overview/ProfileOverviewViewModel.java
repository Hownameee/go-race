package com.grouprace.feature.profile.ui.main.overview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.FollowRepository;
import com.grouprace.core.data.repository.UserRepository;
import com.grouprace.core.model.Profile.ProfileOverview;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileOverviewViewModel extends ViewModel {
  private final UserRepository userRepository;
  private final FollowRepository followRepository;
  private final MutableLiveData<Result<ProfileOverview>> overview = new MutableLiveData<>();
  private LiveData<Result<ProfileOverview>> overviewSource;
  private Observer<Result<ProfileOverview>> overviewObserver;
  private int userId = -1;
  private boolean self = true;

  @Inject
  public ProfileOverviewViewModel(UserRepository userRepository, FollowRepository followRepository) {
    this.userRepository = userRepository;
    this.followRepository = followRepository;
  }

  public LiveData<Result<ProfileOverview>> getOverview() {
    return overview;
  }

  public void initialize(int userId, boolean self) {
    this.userId = userId;
    this.self = self;
  }

  public void loadOverview() {
    if (!self && userId <= 0) {
      return;
    }
    if (overviewSource != null && overviewObserver != null) {
      overviewSource.removeObserver(overviewObserver);
    }

    overviewSource = self ? userRepository.getMyOverview() : userRepository.getUserOverview(userId);
    overviewObserver = result -> overview.setValue(result);
    overviewSource.observeForever(overviewObserver);
  }

  public LiveData<Result<Boolean>> followUser() {
    return followRepository.followUser(userId);
  }

  public LiveData<Result<Boolean>> unfollowUser() {
    return followRepository.unfollowUser(userId);
  }

  public void applyFollowingState(boolean isFollowing) {
    Result<ProfileOverview> current = overview.getValue();
    if (!(current instanceof Result.Success)) {
      return;
    }

    ProfileOverview data = ((Result.Success<ProfileOverview>) current).data;
    if (data == null) {
      return;
    }

    boolean wasFollowing = data.isFollowing();
    data.setFollowing(isFollowing);
    if (wasFollowing != isFollowing) {
      data.setTotalFollowers(Math.max(0, data.getTotalFollowers() + (isFollowing ? 1 : -1)));
    }
    overview.setValue(new Result.Success<>(data));
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (overviewSource != null && overviewObserver != null) {
      overviewSource.removeObserver(overviewObserver);
    }
  }
}
