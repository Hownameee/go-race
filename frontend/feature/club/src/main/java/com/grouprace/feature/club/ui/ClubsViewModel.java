package com.grouprace.feature.club.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.ClubRepository;
import com.grouprace.core.model.Club;

import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ClubsViewModel extends ViewModel {

    private final ClubRepository clubRepository;

    private final MutableLiveData<Boolean> isDiscoverMode = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> syncTrigger = new MutableLiveData<>();
    private final LiveData<Result<String>> syncStatus;
    private final LiveData<List<Club>> clubs;
    private final int LIMIT = 10;
    private final MutableLiveData<Integer> limitLiveData = new MutableLiveData<>(LIMIT);

    @Inject
    public ClubsViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;

        this.clubs = Transformations.switchMap(limitLiveData, limit ->
                Transformations.switchMap(isDiscoverMode, discover -> {
                    if (discover != null && discover) {
                        return clubRepository.getLocalDiscoverClubs(limit);
                    } else {
                        return clubRepository.getLocalMyClubs(limit);
                    }
                })
        );

        // Each time syncTrigger fires, call the unified syncClubs endpoint
        this.syncStatus = Transformations.switchMap(syncTrigger, offset ->
                clubRepository.syncClubs(offset, LIMIT)
        );

        sync();
    }

    public LiveData<Boolean> getIsDiscoverMode() {
        return isDiscoverMode;
    }

    public void setDiscoverMode(boolean isDiscover) {
        if (isDiscoverMode.getValue() == null || isDiscoverMode.getValue() != isDiscover) {
            isDiscoverMode.setValue(isDiscover);
        }
    }

    public LiveData<List<Club>> getClubs() {
        return clubs;
    }

    public LiveData<Result<String>> getSyncStatus() {
        return syncStatus;
    }

    public void sync() {
        int limit = limitLiveData.getValue() != null ? limitLiveData.getValue() : LIMIT;
        syncTrigger.setValue(Math.max(0, limit - LIMIT));
    }

    public void loadMore(int currentListSize) {
        int currentLimit = limitLiveData.getValue() != null ? limitLiveData.getValue() : LIMIT;
        if (currentListSize < currentLimit) {
            return;
        }
        limitLiveData.setValue(currentLimit + LIMIT);
        sync();
    }

    public LiveData<Result<Boolean>> joinClub(String clubId) {
        return clubRepository.joinClub(clubId);
    }
}
