package com.grouprace.feature.club.ui.detail.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.ClubRepository;
import com.grouprace.core.network.model.club.NetworkEventStats;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EventDetailViewModel extends ViewModel {
    private final ClubRepository clubRepository;

    @Inject
    public EventDetailViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public LiveData<Result<com.grouprace.core.model.EventStats>> syncEventStats(int clubId, int eventId) {
        return clubRepository.syncEventStats(clubId, eventId);
    }

    public LiveData<com.grouprace.core.model.ClubEvent> getLocalEvent(int clubId, int eventId) {
        return androidx.lifecycle.Transformations.map(clubRepository.getLocalEvents(clubId), events -> {
            if (events == null) return null;
            return events.stream()
                .filter(e -> e.getEventId() == eventId)
                .findFirst()
                .orElse(null);
        });
    }

    public LiveData<Result<String>> joinEvent(int clubId, int eventId) {
        return clubRepository.joinEvent(clubId, eventId);
    }
}
