package com.grouprace.feature.search.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.SearchRepository;
import com.grouprace.core.model.UserSearchResult;

import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SearchViewModel extends ViewModel {

    private final SearchRepository searchRepository;

    // --- Triggers ---
    private final MutableLiveData<Void> suggestedFriendsTrigger = new MutableLiveData<>();
    private final MutableLiveData<Void> suggestedClubsTrigger = new MutableLiveData<>();
    private final MutableLiveData<String> searchFriendsTrigger = new MutableLiveData<>();
    private final MutableLiveData<String> searchClubsTrigger = new MutableLiveData<>();

    // --- LiveData Kết quả ---
    private final LiveData<Result<List<UserSearchResult>>> suggestedFriends;
    private final LiveData<Result<List<UserSearchResult>>> suggestedClubs;
    private final LiveData<Result<List<UserSearchResult>>> searchFriendsResults;
    private final LiveData<Result<List<UserSearchResult>>> searchClubsResults;

    // Trạng thái Tab (Friends = false, Clubs = true)
    private final MutableLiveData<Boolean> isClubTab = new MutableLiveData<>(false);

    @Inject
    public SearchViewModel(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;

        // 1. Listeners cho Tab Friends
        this.suggestedFriends = Transformations.switchMap(suggestedFriendsTrigger, v ->
                searchRepository.getSuggestedUsers()
        );
        this.searchFriendsResults = Transformations.switchMap(searchFriendsTrigger, query ->
                searchRepository.searchUsers(query)
        );

        // 2. Listeners cho Tab Clubs
        this.suggestedClubs = Transformations.switchMap(suggestedClubsTrigger, v ->
                searchRepository.getSuggestedClubs()
        );
        this.searchClubsResults = Transformations.switchMap(searchClubsTrigger, query ->
                searchRepository.searchClubs(query)
        );

        // Khởi tạo dữ liệu mặc định
        fetchSuggestedFriends();
    }

    // --- Getters ---
    public LiveData<Boolean> getIsClubTab() { return isClubTab; }

    public LiveData<Result<List<UserSearchResult>>> getSuggestedFriends() { return suggestedFriends; }
    public LiveData<Result<List<UserSearchResult>>> getSuggestedClubs() { return suggestedClubs; }

    public LiveData<Result<List<UserSearchResult>>> getSearchFriendsResults() { return searchFriendsResults; }
    public LiveData<Result<List<UserSearchResult>>> getSearchClubsResults() { return searchClubsResults; }

    // --- Actions ---

    public void fetchSuggestedFriends() {
        isClubTab.setValue(false);
        suggestedFriendsTrigger.setValue(null);
    }

    public void fetchSuggestedClubs() {
        isClubTab.setValue(true);
        suggestedClubsTrigger.setValue(null);
    }

    public void search(String query) {
        boolean isClub = Boolean.TRUE.equals(isClubTab.getValue());

        if (query == null || query.trim().isEmpty()) {
            if (isClub) fetchSuggestedClubs();
            else fetchSuggestedFriends();
            return;
        }

        if (isClub) {
            searchClubsTrigger.setValue(query);
        } else {
            searchFriendsTrigger.setValue(query);
        }
    }

    // --- Follow Actions ---
    public LiveData<Result<Boolean>> followUser(int userId) {
        return searchRepository.followUser(userId);
    }

    public LiveData<Result<Boolean>> unfollowUser(int userId) {
        return searchRepository.unfollowUser(userId);
    }
}