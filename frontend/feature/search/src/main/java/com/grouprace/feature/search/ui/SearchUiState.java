package com.grouprace.feature.search.ui;

import com.grouprace.core.model.UserSearchResult;

import java.util.List;

public class SearchUiState {
    public List<UserSearchResult> data;
    public boolean isLoading;
    public String error;
    public boolean isSearching;

    public SearchUiState(List<UserSearchResult> data,
                         boolean isLoading,
                         String error,
                         boolean isSearching) {
        this.data = data;
        this.isLoading = isLoading;
        this.error = error;
        this.isSearching = isSearching;
    }
}
