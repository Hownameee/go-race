package com.grouprace.feature.club.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.ClubRepository;
import com.grouprace.core.model.Club;
import com.grouprace.core.model.ClubEvent;
import com.grouprace.core.model.ClubStats;
import com.grouprace.core.model.Post;
import com.grouprace.core.model.Record;

import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ClubDetailViewModel extends ViewModel {

    private final ClubRepository clubRepository;

    private final MutableLiveData<String> currentClubId = new MutableLiveData<>();

    @Inject
    public ClubDetailViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public void setClubId(String clubId) {
        currentClubId.setValue(clubId);
    }

    public LiveData<Result<Club>> getClubDetails() {
        return Transformations.switchMap(currentClubId, clubRepository::getClubDetails);
    }

    public LiveData<Result<List<Post>>> getPosts() {
        return Transformations.switchMap(currentClubId, clubRepository::getClubPosts);
    }

    public LiveData<Result<List<Record>>> getActivities() {
        return Transformations.switchMap(currentClubId, clubRepository::getClubActivities);
    }

    public LiveData<Result<List<ClubEvent>>> getEvents() {
        return Transformations.switchMap(currentClubId, clubRepository::getClubEvents);
    }

    public LiveData<Result<ClubStats>> getClubStats() {
        return Transformations.switchMap(currentClubId, clubRepository::getClubStats);
    }
    
    public LiveData<Result<Boolean>> leaveClub() {
        String clubId = currentClubId.getValue();
        if(clubId != null) {
            return clubRepository.leaveClub(clubId);
        }
        return new MutableLiveData<>();
    }

    public LiveData<Result<Boolean>> deleteClub() {
        String clubId = currentClubId.getValue();
        if(clubId != null) {
            return clubRepository.deleteClub(clubId);
        }
        return new MutableLiveData<>();
    }
}
