package com.grouprace.feature.club.ui.detail.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.ClubRepository;
import com.grouprace.core.model.Club;
import com.grouprace.core.model.ClubStats;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ClubStatisticsViewModel extends ViewModel {
    private final ClubRepository clubRepository;

    @Inject
    public ClubStatisticsViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public LiveData<Club> getLocalClubById(int clubId) {
        return clubRepository.getLocalClubById(clubId);
    }

    public LiveData<List<ClubStats.LeaderboardEntry>> getLocalLeaderboard(int clubId) {
        return clubRepository.getLocalLeaderboard(clubId);
    }

    public LiveData<Result<String>> syncClubStats(int clubId) {
        return clubRepository.syncClubStats(clubId);
    }
}
