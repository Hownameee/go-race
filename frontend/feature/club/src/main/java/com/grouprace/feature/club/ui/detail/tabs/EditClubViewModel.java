package com.grouprace.feature.club.ui.detail.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.ClubRepository;
import com.grouprace.core.model.Club;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditClubViewModel extends ViewModel {
    private final ClubRepository clubRepository;

    @Inject
    public EditClubViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public LiveData<Club> getClub(int clubId) {
        return clubRepository.getLocalClubById(clubId);
    }

    public LiveData<Result<String>> updateClub(int clubId, String name, String description, byte[] imageBytes, String mimeType) {
        return clubRepository.updateClub(clubId, name, description, imageBytes, mimeType);
    }
}
