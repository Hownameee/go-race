package com.grouprace.core.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.UserSearchResult;
import com.grouprace.core.network.model.search.NetworkUserSearch;
import com.grouprace.core.network.source.SearchNetworkDataSource;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class SearchRepositoryImpl implements SearchRepository {

    private final SearchNetworkDataSource searchNetworkDataSource;

    @Inject
    public SearchRepositoryImpl(SearchNetworkDataSource searchNetworkDataSource) {
        this.searchNetworkDataSource = searchNetworkDataSource;
    }

    @Override
    public LiveData<Result<List<UserSearchResult>>> searchUsers(String query) {
        return Transformations.map(searchNetworkDataSource.searchUsers(query), result -> {
            if (result instanceof Result.Success) {
                List<NetworkUserSearch> networkUsers = ((Result.Success<List<NetworkUserSearch>>) result).data;
                List<UserSearchResult> users = networkUsers.stream()
                        .map(NetworkUserSearch::asExternalModel)
                        .collect(Collectors.toList());
                return new Result.Success<>(users);
            } else if (result instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) result;
                return new Result.Error<>(error.exception, error.message);
            } else {
                return new Result.Loading<>();
            }
        });
    }

    @Override
    public LiveData<Result<List<UserSearchResult>>> getSuggestedUsers() {
        return Transformations.map(searchNetworkDataSource.getSuggestedUsers(), result -> {
            if (result instanceof Result.Success) {
                List<NetworkUserSearch> networkUsers = ((Result.Success<List<NetworkUserSearch>>) result).data;
                List<UserSearchResult> users = networkUsers.stream()
                        .map(NetworkUserSearch::asExternalModel)
                        .collect(Collectors.toList());
                return new Result.Success<>(users);
            } else if (result instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) result;
                return new Result.Error<>(error.exception, error.message);
            } else {
                return new Result.Loading<>();
            }
        });
    }

    @Override
    public LiveData<Result<Boolean>> followUser(int targetUserId) {
        return searchNetworkDataSource.followUser(targetUserId);
    }

    @Override
    public LiveData<Result<Boolean>> unfollowUser(int targetUserId) {
        return searchNetworkDataSource.unfollowUser(targetUserId);
    }

    // --- CLUB METHODS ---

    @Override
    public LiveData<Result<List<UserSearchResult>>> searchClubs(String query) {
        return Transformations.map(searchNetworkDataSource.searchClubs(query), result -> {
            if (result instanceof Result.Success) {
                List<NetworkUserSearch> networkClubs = ((Result.Success<List<NetworkUserSearch>>) result).data;
                List<UserSearchResult> clubs = networkClubs.stream()
                        .map(NetworkUserSearch::asExternalModel)
                        .collect(Collectors.toList());
                return new Result.Success<>(clubs);
            } else if (result instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) result;
                return new Result.Error<>(error.exception, error.message);
            } else {
                return new Result.Loading<>();
            }
        });
    }

    @Override
    public LiveData<Result<List<UserSearchResult>>> getSuggestedClubs() {
        return Transformations.map(searchNetworkDataSource.getSuggestedClubs(), result -> {
            if (result instanceof Result.Success) {
                List<NetworkUserSearch> networkClubs = ((Result.Success<List<NetworkUserSearch>>) result).data;
                List<UserSearchResult> clubs = networkClubs.stream()
                        .map(NetworkUserSearch::asExternalModel)
                        .collect(Collectors.toList());
                return new Result.Success<>(clubs);
            } else if (result instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) result;
                return new Result.Error<>(error.exception, error.message);
            } else {
                return new Result.Loading<>();
            }
        });
    }

    @Override
    public LiveData<Result<Boolean>> joinClub(int clubId) {
        return searchNetworkDataSource.joinClub(clubId);
    }

    @Override
    public LiveData<Result<Boolean>> leaveClub(int clubId) {
        return searchNetworkDataSource.leaveClub(clubId);
    }
}