package com.grouprace.feature.club.ui.detail.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.ClubRepository;
import com.grouprace.core.model.ClubEvent;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ClubEventsViewModel extends ViewModel {
    private final ClubRepository clubRepository;

    @Inject
    public ClubEventsViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public LiveData<List<ClubEvent>> getLocalEvents(int clubId) {
        return clubRepository.getLocalEvents(clubId);
    }

    public LiveData<Result<String>> syncEvents(int clubId) {
        return clubRepository.syncEvents(clubId);
    }

    public LiveData<Result<String>> joinEvent(int clubId, int eventId) {
        return clubRepository.joinEvent(clubId, eventId);
    }

    public LiveData<Result<Boolean>> checkIsLeader(int clubId) {
        return clubRepository.checkIsLeader(clubId);
    }
}
