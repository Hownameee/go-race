package com.grouprace.feature.club.ui.detail.tabs;

import androidx.lifecycle.LiveData;
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

    @Inject
    public OverviewViewModel(ClubRepository repository) {
        this.repository = repository;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
        repository.syncAdmins(clubId); // Trigger sync on initialization
        repository.syncClubById(clubId); // Update club metadata as well
    }

    public LiveData<Club> getClub() {
        return repository.getLocalClubById(clubId);
    }

    public LiveData<List<ClubAdmin>> getAdmins() {
        return repository.getAdminsForClub(clubId);
    }

    public LiveData<Result<String>> leaveClub() {
        return repository.leaveClub(String.valueOf(clubId));
    }
}
