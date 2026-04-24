package com.grouprace.feature.club.ui.detail.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.ClubRepository;
import com.grouprace.core.model.Club;
import com.grouprace.core.model.ClubAdmin;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OverviewViewModel extends ViewModel {

    private final ClubRepository repository;
    private int clubId;
    private final MutableLiveData<Boolean> isLeader = new MutableLiveData<>(false);

    @Inject
    public OverviewViewModel(ClubRepository repository) {
        this.repository = repository;
    }

    private final java.util.List<androidx.lifecycle.Observer<Result<Boolean>>> activeObservers = new java.util.ArrayList<>();

    public void setClubId(int clubId) {
        if (this.clubId == clubId) return;
        this.clubId = clubId;
        repository.syncAdmins(clubId);
        repository.syncClubById(clubId);
        
        androidx.lifecycle.Observer<Result<Boolean>> observer = result -> {
            if (result instanceof Result.Success) {
                isLeader.postValue(((Result.Success<Boolean>) result).data);
            }
        };
        activeObservers.add(observer);
        repository.checkIsLeader(clubId).observeForever(observer);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        for (androidx.lifecycle.Observer<Result<Boolean>> observer : activeObservers) {
            repository.checkIsLeader(clubId).removeObserver(observer);
        }
        activeObservers.clear();
    }

    public LiveData<Club> getClub() {
        return repository.getLocalClubById(clubId);
    }

    public LiveData<List<ClubAdmin>> getAdmins() {
        return repository.getAdminsForClub(clubId);
    }

    public LiveData<Boolean> getIsLeader() {
        return isLeader;
    }

    public LiveData<Result<String>> leaveClub() {
        return repository.leaveClub(String.valueOf(clubId));
    }
}
