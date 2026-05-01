package com.grouprace.feature.search.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.FollowRepository;
import com.grouprace.core.data.repository.SearchRepository;
import com.grouprace.core.model.UserSearchResult;

import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class SearchViewModel extends ViewModel {

    private final SearchRepository repository;
    private final FollowRepository followRepository;
    private final MutableLiveData<SearchUiState> uiState = new MutableLiveData<>();

    private boolean isClubTab = false;

    @Inject
    public SearchViewModel(SearchRepository repository, FollowRepository followRepository) {
        this.repository = repository;
        this.followRepository = followRepository;
        loadSuggested();
    }

    public LiveData<SearchUiState> getUiState() {
        return uiState;
    }

    public void switchTab(boolean isClub) {
        this.isClubTab = isClub;
        loadSuggested();
    }

    private void loadSuggested() {
        uiState.setValue(new SearchUiState(null, true, null, false));

        LiveData<Result<List<UserSearchResult>>> source = isClubTab
                ? repository.getSuggestedClubs()
                : repository.getSuggestedUsers();

        source.observeForever(result -> {
            if (result instanceof Result.Success) {
                uiState.postValue(new SearchUiState(
                        ((Result.Success<List<UserSearchResult>>) result).data,
                        false,
                        null,
                        false
                ));
            } else if (result instanceof Result.Error) {
                uiState.postValue(new SearchUiState(
                        null,
                        false,
                        ((Result.Error<?>) result).message,
                        false
                ));
            }
        });
    }

    public void search(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadSuggested();
            return;
        }

        uiState.setValue(new SearchUiState(null, true, null, true));

        LiveData<Result<List<UserSearchResult>>> source = isClubTab
                ? repository.searchClubs(query)
                : repository.searchUsers(query);

        source.observeForever(result -> {
            if (result instanceof Result.Success) {
                uiState.postValue(new SearchUiState(
                        ((Result.Success<List<UserSearchResult>>) result).data,
                        false,
                        null,
                        true
                ));
            } else if (result instanceof Result.Error) {
                uiState.postValue(new SearchUiState(
                        null,
                        false,
                        ((Result.Error<?>) result).message,
                        true
                ));
            }
        });
    }

    public LiveData<Result<Boolean>> followUser(int id) {
        return followRepository.followUser(id);
    }

    public LiveData<Result<Boolean>> unfollowUser(int id) {
        return followRepository.unfollowUser(id);
    }
}
