package com.grouprace.feature.club.ui.detail.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.ClubRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CreateEventViewModel extends ViewModel {
    private final ClubRepository clubRepository;

    @Inject
    public CreateEventViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public LiveData<Result<String>> createEvent(int clubId, String title, String description, double targetDistance, int targetDurationSeconds, String startTime, String endTime) {
        return clubRepository.createEvent(clubId, title, description, targetDistance, targetDurationSeconds, startTime, endTime);
    }
}
