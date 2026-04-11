package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.dao.ClubDao;
import com.grouprace.core.data.model.ClubEntity;
import com.grouprace.core.model.Club;
import com.grouprace.core.model.ClubEvent;
import com.grouprace.core.model.ClubStats;
import com.grouprace.core.model.Post;
import com.grouprace.core.model.Record;
import com.grouprace.core.network.model.club.ClubListPayload;
import com.grouprace.core.network.model.club.NetworkClub;
import com.grouprace.core.network.source.ClubNetworkDataSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class ClubRepositoryImpl implements ClubRepository {
    private static final String TAG = "ClubRepositoryImpl";
    private final ClubNetworkDataSource networkDataSource;
    private final ClubDao clubDao;

    @Inject
    public ClubRepositoryImpl(ClubNetworkDataSource networkDataSource, ClubDao clubDao) {
        this.networkDataSource = networkDataSource;
        this.clubDao = clubDao;
    }

    @Override
    public LiveData<List<Club>> getLocalMyClubs(int limit) {
        return Transformations.map(clubDao.getMyClubs(),
                entities -> entities.stream().map(ClubEntity::asExternalModel).collect(Collectors.toList()));
    }

    @Override
    public LiveData<List<Club>> getLocalDiscoverClubs(int limit) {
        return Transformations.map(clubDao.getDiscoverClubs(),
                entities -> entities.stream().map(ClubEntity::asExternalModel).collect(Collectors.toList()));
    }

    @Override
    public LiveData<Result<String>> syncClubs(int offset, int limit) {
        MutableLiveData<Result<String>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        networkDataSource.getClubs(offset, limit).observeForever(networkResult -> {
            if (networkResult instanceof Result.Success) {
                ClubListPayload payload = ((Result.Success<ClubListPayload>) networkResult).data;
                String type = payload.getType();
                List<NetworkClub> networkClubs = payload.getClubs();

                List<ClubEntity> entities = networkClubs != null
                        ? networkClubs.stream().map(n -> toEntity(n, "my clubs".equals(type)))
                                      .collect(Collectors.toList())
                        : Collections.emptyList();

                // Persist on a background thread
                Executors.newSingleThreadExecutor().execute(() -> {
                    if (offset == 0) {
                        // Replace the stale page on first load
                        if ("my clubs".equals(type)) {
                            clubDao.deleteAllMyClubs();
                        } else {
                            clubDao.deleteAllDiscoverClubs();
                        }
                    }
                    clubDao.insertClubs(entities);
                });

                result.postValue(new Result.Success<>(type));
            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                result.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return result;
    }

    // ── Stub implementations (detail screens use these) ───────────────────────

    @Override
    public LiveData<Result<Club>> getClubDetails(String clubId) {
        MutableLiveData<Result<Club>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Error<>(null, "Not implemented"));
        return liveData;
    }

    @Override
    public LiveData<Result<List<Post>>> getClubPosts(String clubId) {
        MutableLiveData<Result<List<Post>>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Success<>(new ArrayList<>()));
        return liveData;
    }

    @Override
    public LiveData<Result<List<Record>>> getClubActivities(String clubId) {
        MutableLiveData<Result<List<Record>>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Success<>(new ArrayList<>()));
        return liveData;
    }

    @Override
    public LiveData<Result<List<ClubEvent>>> getClubEvents(String clubId) {
        MutableLiveData<Result<List<ClubEvent>>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Success<>(new ArrayList<>()));
        return liveData;
    }

    @Override
    public LiveData<Result<ClubStats>> getClubStats(String clubId) {
        MutableLiveData<Result<ClubStats>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Error<>(null, "Not implemented"));
        return liveData;
    }

    @Override
    public LiveData<Result<Boolean>> joinClub(String clubId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Success<>(true));
        return liveData;
    }

    @Override
    public LiveData<Result<Boolean>> leaveClub(String clubId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Success<>(true));
        return liveData;
    }

    @Override
    public LiveData<Result<Boolean>> deleteClub(String clubId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Success<>(true));
        return liveData;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private ClubEntity toEntity(NetworkClub n, boolean isJoined) {
        return new ClubEntity(
                n.getClubId(),
                n.getName(),
                n.getDescription(),
                n.getPrivacyType(),
                n.getLeaderId(),
                n.getLeaderName(),
                n.getMemberCount(),
                n.getPostCount(),
                n.getAvatarUrl(),
                isJoined
        );
    }

    private <T> List<T> safeList(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }
}
