package com.grouprace.feature.profile.ui.clubs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.ClubRepository;
import com.grouprace.core.model.Club;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileClubsViewModel extends ViewModel {
    private static final int PAGE_SIZE = 10;

    private final ClubRepository clubRepository;
    private final MutableLiveData<Integer> limitLiveData = new MutableLiveData<>(PAGE_SIZE);
    private final MutableLiveData<Integer> syncTrigger = new MutableLiveData<>();
    private final LiveData<Result<String>> syncStatus;
    private LiveData<List<Club>> clubs = new MutableLiveData<>(Collections.emptyList());
    private boolean self = true;
    private boolean initialized;

    @Inject
    public ProfileClubsViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
        this.syncStatus = Transformations.switchMap(syncTrigger, offset -> {
            if (!self) {
                MutableLiveData<Result<String>> result = new MutableLiveData<>();
                result.setValue(new Result.Success<>("unsupported"));
                return result;
            }
            return clubRepository.syncClubs(offset, PAGE_SIZE);
        });
    }

    public void initialize(boolean self) {
        if (initialized && this.self == self) {
            return;
        }
        this.self = self;
        this.initialized = true;
        clubs = self
                ? Transformations.switchMap(limitLiveData, clubRepository::getLocalMyClubs)
                : new MutableLiveData<>(Collections.emptyList());
    }

    public LiveData<List<Club>> getClubs() {
        return clubs;
    }

    public LiveData<Result<String>> getSyncStatus() {
        return syncStatus;
    }

    public void sync() {
        int limit = limitLiveData.getValue() != null ? limitLiveData.getValue() : PAGE_SIZE;
        syncTrigger.setValue(Math.max(0, limit - PAGE_SIZE));
    }

    public void loadMore(int currentListSize) {
        if (!self) {
            return;
        }
        int currentLimit = limitLiveData.getValue() != null ? limitLiveData.getValue() : PAGE_SIZE;
        if (currentListSize < currentLimit) {
            return;
        }
        limitLiveData.setValue(currentLimit + PAGE_SIZE);
        sync();
    }
}
