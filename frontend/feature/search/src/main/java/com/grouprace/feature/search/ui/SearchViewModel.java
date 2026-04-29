package com.grouprace.feature.search.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.SearchRepository;
import com.grouprace.core.model.UserSearchResult;

import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class SearchViewModel extends ViewModel {

    private final SearchRepository repository;

    private final MutableLiveData<SearchUiState> uiState = new MutableLiveData<>();

    private List<UserSearchResult> suggestedUsersCache;
    private List<UserSearchResult> suggestedClubsCache;

    private boolean isClubTab = false;
    
    public boolean isClubTab() {
        return isClubTab;
    }

    @Inject
    public SearchViewModel(SearchRepository repository) {
        this.repository = repository;
        loadSuggested();
    }

    public LiveData<SearchUiState> getUiState() {
        return uiState;
    }

    // --- Tab ---
    public void switchTab(boolean isClub) {
        this.isClubTab = isClub;
        
        // Use cache if available to avoid unnecessary network calls and state loss
        if (isClubTab && suggestedClubsCache != null) {
            uiState.setValue(new SearchUiState(suggestedClubsCache, false, null, false));
        } else if (!isClubTab && suggestedUsersCache != null) {
            uiState.setValue(new SearchUiState(suggestedUsersCache, false, null, false));
        } else {
            loadSuggested();
        }
    }

    // --- Suggested ---
    private void loadSuggested() {
        uiState.setValue(new SearchUiState(null, true, null, false));

        LiveData<Result<List<UserSearchResult>>> source =
                isClubTab ? repository.getSuggestedClubs()
                        : repository.getSuggestedUsers();

        source.observeForever(result -> {
            if (result instanceof Result.Success) {
                List<UserSearchResult> data = ((Result.Success<List<UserSearchResult>>) result).data;
                if (isClubTab) {
                    suggestedClubsCache = data;
                } else {
                    suggestedUsersCache = data;
                }
                uiState.postValue(new SearchUiState(data, false, null, false));
            } else if (result instanceof Result.Error) {
                uiState.postValue(new SearchUiState(null, false, ((Result.Error<?>) result).message, false));
            }
        });
    }
    
    public void updateItemStatus(int id, int status) {
        SearchUiState current = uiState.getValue();
        if (current != null && current.data != null) {
            for (UserSearchResult item : current.data) {
                if (item.getUserId() == id) {
                    item.setFollowStatus(status);
                    break;
                }
            }
            uiState.setValue(current);
        }
        
        // Also update cache
        List<UserSearchResult> cache = isClubTab ? suggestedClubsCache : suggestedUsersCache;
        if (cache != null) {
            for (UserSearchResult item : cache) {
                if (item.getUserId() == id) {
                    item.setFollowStatus(status);
                    break;
                }
            }
        }
    }

    // --- Search ---
    public void search(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadSuggested();
            return;
        }

        uiState.setValue(new SearchUiState(null, true, null, true));

        LiveData<Result<List<UserSearchResult>>> source =
                isClubTab ? repository.searchClubs(query)
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

    // --- Follow ---
    public LiveData<Result<Boolean>> followUser(int id) {
        return repository.followUser(id);
    }

    public LiveData<Result<Boolean>> unfollowUser(int id) {
        return repository.unfollowUser(id);
    }

    public LiveData<Result<String>> joinClub(int id) {
        return repository.joinClub(id);
    }

    public LiveData<Result<String>> leaveClub(int id) {
        return repository.leaveClub(id);
    }
}