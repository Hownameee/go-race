package com.grouprace.feature.club.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.data.repository.ClubRepository;
import com.grouprace.core.model.Club;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ClubDetailViewModel extends ViewModel {
    private final ClubRepository clubRepository;
    private final MutableLiveData<Integer> clubIdTrigger = new MutableLiveData<>();
    private final LiveData<Club> club;

    @Inject
    public ClubDetailViewModel(ClubRepository repo) {
        this.clubRepository = repo;

        // Listen to changes on clubIdTrigger
        this.club = Transformations.switchMap(clubIdTrigger, clubId -> {
            clubRepository.syncClubById(clubId);
            return clubRepository.getLocalClubById(clubId);
        });
    }
    public void loadClub(int clubId) {
        if (clubIdTrigger.getValue() == null || !clubIdTrigger.getValue().equals(clubId)) {
            clubIdTrigger.setValue(clubId);
        }
    }

    public void joinClub() {
        if (clubIdTrigger.getValue() != null) {
            clubRepository.joinClub(String.valueOf(clubIdTrigger.getValue()));
        }
    }

    public void leaveClub() {
        if (clubIdTrigger.getValue() != null) {
            clubRepository.leaveClub(String.valueOf(clubIdTrigger.getValue()));
            clubRepository.syncClubById(clubIdTrigger.getValue());
        }
    }

    public LiveData<Club> getClub() {
        return club;
    }
}