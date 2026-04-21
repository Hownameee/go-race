package com.grouprace.feature.club.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.ClubRepository;
import com.grouprace.core.data.repository.PostRepository;
import com.grouprace.core.model.Club;
import com.grouprace.core.model.Post;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ClubDetailViewModel extends ViewModel {
    private final ClubRepository clubRepository;
    private final PostRepository postRepository;
    private final MutableLiveData<Integer> clubIdTrigger = new MutableLiveData<>();
    private final MutableLiveData<String> syncTrigger = new MutableLiveData<>();
    private final LiveData<Club> club;
    private final LiveData<List<Post>> clubPosts;
    private final LiveData<Result<Boolean>> syncStatus;
    private final int LIMIT = 20;

    @Inject
    public ClubDetailViewModel(ClubRepository repo, PostRepository postRepository) {
        this.clubRepository = repo;
        this.postRepository = postRepository;

        // Listen to changes on clubIdTrigger
        this.club = Transformations.switchMap(clubIdTrigger, clubId -> {
            clubRepository.syncClubById(clubId);
            return clubRepository.getLocalClubById(clubId);
        });

        this.clubPosts = Transformations.switchMap(clubIdTrigger, clubId -> postRepository.getPostsByClubId(clubId));

        this.syncStatus = Transformations.switchMap(syncTrigger, cursor -> {
            Integer clubId = clubIdTrigger.getValue();
            if (clubId != null) {
                return postRepository.syncClubPosts(clubId, cursor, LIMIT);
            }
            return new MutableLiveData<>(new Result.Error<>(new Exception("Club not loaded"), "Club not loaded"));
        });
    }

    public void loadClub(int clubId) {
        if (clubIdTrigger.getValue() == null || !clubIdTrigger.getValue().equals(clubId)) {
            clubIdTrigger.setValue(clubId);
            fetchPosts(null); // Initial fetch
        }
    }

    public void fetchPosts(String cursor) {
        syncTrigger.setValue(cursor);
    }

    public LiveData<Result<Boolean>> getSyncStatus() {
        return syncStatus;
    }

    public LiveData<Result<Boolean>> likePost(int postId) {
        return postRepository.likePost(postId);
    }

    public LiveData<Result<Boolean>> unlikePost(int postId) {
        return postRepository.unlikePost(postId);
    }

    public void joinClub() {
        if (clubIdTrigger.getValue() != null) {
            clubRepository.joinClub(String.valueOf(clubIdTrigger.getValue()));
        }
    }

    public void leaveClub() {
        if (clubIdTrigger.getValue() != null) {
            clubRepository.leaveClub(String.valueOf(clubIdTrigger.getValue()));
            clubRepository.syncClubById(clubIdTrigger.getValue());
        }
    }

    public LiveData<Club> getClub() {
        return club;
    }

    public LiveData<List<Post>> getClubPosts() {
        return clubPosts;
    }

    public LiveData<List<com.grouprace.core.model.ClubStats.LeaderboardEntry>> getLocalLeaderboard(int clubId) {
        return clubRepository.getLocalLeaderboard(clubId);
    }

    public LiveData<Result<String>> syncClubStats(int clubId) {
        return clubRepository.syncClubStats(clubId);
    }
}