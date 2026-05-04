package com.grouprace.feature.profile.ui.main.streak;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.RecordRepository;
import com.grouprace.core.network.model.record.RecordStreakResponse;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileStreakViewModel extends ViewModel {
    private final RecordRepository recordRepository;
    private final MutableLiveData<Result<RecordStreakResponse>> streak = new MutableLiveData<>();
    private LiveData<Result<RecordStreakResponse>> streakSource;
    private Observer<Result<RecordStreakResponse>> streakObserver;
    private int userId = -1;
    private boolean self = true;

    @Inject
    public ProfileStreakViewModel(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public LiveData<Result<RecordStreakResponse>> getStreak() {
        return streak;
    }

    public void initialize(int userId, boolean self) {
        this.userId = userId;
        this.self = self;
    }

    public void loadStreak() {
        if (!self && userId <= 0) {
            return;
        }
        if (streakSource != null && streakObserver != null) {
            streakSource.removeObserver(streakObserver);
        }

        streakSource = self ? recordRepository.getMyStreak() : recordRepository.getUserStreak(userId);
        streakObserver = result -> streak.setValue(result);
        streakSource.observeForever(streakObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (streakSource != null && streakObserver != null) {
            streakSource.removeObserver(streakObserver);
        }
    }
}
