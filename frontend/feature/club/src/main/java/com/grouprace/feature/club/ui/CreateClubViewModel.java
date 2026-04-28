package com.grouprace.feature.club.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.ClubRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CreateClubViewModel extends ViewModel {

    private final ClubRepository clubRepository;

    @Inject
    public CreateClubViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public LiveData<Result<String>> createClub(String name, String description, String privacyType) {
        return clubRepository.createClub(name, description, privacyType);
    }
}
