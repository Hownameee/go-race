package com.grouprace.core.data.repository;

import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.dao.ClubDao;
import com.grouprace.core.data.dao.ClubAdminDao;
import com.grouprace.core.data.model.ClubAdminEntity;
import com.grouprace.core.data.model.ClubEntity;
import com.grouprace.core.model.Club;
import com.grouprace.core.model.ClubAdmin;
import com.grouprace.core.network.model.club.ClubListPayload;
import com.grouprace.core.network.model.club.ClubPayload;
import com.grouprace.core.network.model.club.JoinClubResponse;
import com.grouprace.core.network.model.club.NetworkClub;
import com.grouprace.core.network.model.club.NetworkClubAdmin;
import com.grouprace.core.network.model.club.UpdateClubRequest;
import com.grouprace.core.network.source.ClubNetworkDataSource;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class ClubRepositoryImpl implements ClubRepository {
    private static final String TAG = "ClubRepositoryImpl";
    private final ClubNetworkDataSource networkDataSource;
    private final ClubDao clubDao;
    private final ClubAdminDao clubAdminDao;

    @Inject
    public ClubRepositoryImpl(ClubNetworkDataSource networkDataSource, ClubDao clubDao, ClubAdminDao clubAdminDao) {
        this.networkDataSource = networkDataSource;
        this.clubDao = clubDao;
        this.clubAdminDao = clubAdminDao;
    }

    @Override
    public LiveData<List<Club>> getLocalMyClubs(int limit) {
        return Transformations.map(clubDao.getMyClubs(), entities -> entities.stream().map(ClubEntity::asExternalModel).collect(Collectors.toList()));
    }

    @Override
    public LiveData<Club> getLocalClubById(int clubId) {
        return Transformations.map(clubDao.getClubById(clubId), ClubEntity::asExternalModel);
    }


    @Override
    public LiveData<List<Club>> getLocalDiscoverClubs(int limit) {
        return Transformations.map(clubDao.getDiscoverClubs(), entities -> entities.stream().map(ClubEntity::asExternalModel).collect(Collectors.toList()));
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

                List<ClubEntity> entities = networkClubs != null ? networkClubs.stream().map(this::toEntity).collect(Collectors.toList()) : Collections.emptyList();

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
                    clubDao.upsertClubs(entities);
                });

                result.postValue(new Result.Success<>(type));
            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                result.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return result;
    }

    @Override
    public void syncClubById(int clubId) {
        networkDataSource.getClubById(clubId).observeForever(networkResult -> {
            if (networkResult instanceof Result.Success) {
                ClubPayload payload = ((Result.Success<ClubPayload>) networkResult).data;
                List<NetworkClub> networkClubs = payload.getClubs();

                List<ClubEntity> entities = networkClubs != null ? networkClubs.stream().map(this::toEntity).collect(Collectors.toList()) : Collections.emptyList();

                Executors.newSingleThreadExecutor().execute(() -> {
                    clubDao.upsertClubs(entities);
                });

            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                Log.e(TAG, "syncClubById: Error syncing club with ID " + clubId + ": " + error.message);
            }
        });
    }


    @Override
    public LiveData<Result<String>> joinClub(String clubId) {
        MutableLiveData<Result<String>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        int id = Integer.parseInt(clubId);
        networkDataSource.joinClub(id).observeForever(networkResult -> {
            if (networkResult instanceof Result.Success) {
                String message = ((Result.Success<JoinClubResponse>) networkResult).data.getResult();
                Executors.newSingleThreadExecutor().execute(() -> {
                    if ("Joined".equals(message)) {
                        clubDao.updateStatus(id, "approved");
                    } else if ("Request sent".equals(message)) {
                        clubDao.updateStatus(id, "pending");
                    }
                });
                result.postValue(new Result.Success<>(message));
            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                result.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return result;
    }

    @Override
    public LiveData<Result<String>> leaveClub(String clubId) {
        MutableLiveData<Result<String>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        int id = Integer.parseInt(clubId);
        networkDataSource.leaveClub(id).observeForever(networkResult -> {
            if (networkResult instanceof Result.Success) {
                String message = ((Result.Success<JoinClubResponse>) networkResult).data.getResult();
                Executors.newSingleThreadExecutor().execute(() -> {
                    clubDao.removeStatus(id);
                });
                result.postValue(new Result.Success<>(message));
            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                result.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return result;
    }

    @Override
    public LiveData<Result<String>> createClub(String name, String description, String privacyType) {
        MutableLiveData<Result<String>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        com.grouprace.core.network.model.club.CreateClubRequest request = new com.grouprace.core.network.model.club.CreateClubRequest(name, description, privacyType);
        networkDataSource.createClub(request).observeForever(networkResult -> {
            if (networkResult instanceof Result.Success) {
                syncClubs(0, 10);
                result.postValue(new Result.Success<>(((Result.Success<String>) networkResult).data));
            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                result.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return result;
    }

    @Override
    public LiveData<List<ClubAdmin>> getAdminsForClub(int clubId) {
        return Transformations.map(clubAdminDao.getAdminsForClub(clubId), entities -> 
            entities.stream().map(ClubAdminEntity::asExternalModel).collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<Result<String>> syncAdmins(int clubId) {
        MutableLiveData<Result<String>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        networkDataSource.getAdmins(clubId).observeForever(networkResult -> {
            if (networkResult instanceof Result.Success) {
                List<NetworkClubAdmin> admins = ((Result.Success<List<NetworkClubAdmin>>) networkResult).data;
                List<ClubAdminEntity> entities = admins.stream()
                        .map(n -> new ClubAdminEntity(clubId, n.getUserId(), n.getFullname(), n.getAvatarUrl(), n.isLeader()))
                        .collect(Collectors.toList());

                Executors.newSingleThreadExecutor().execute(() -> {
                    clubAdminDao.replaceAdminsForClub(clubId, entities);
                });
                result.postValue(new Result.Success<>("Admins synced"));
            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                result.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return result;
    }

    @Override
    public LiveData<Result<Boolean>> checkIsLeader(int clubId) {
        return networkDataSource.checkIsLeader(clubId);
    }

    @Override
    public LiveData<Result<String>> updateClub(int clubId, String name, String description, byte[] imageBytes, String mimeType) {
        MutableLiveData<Result<String>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        String imageBase64 = null;
        if (imageBytes != null && imageBytes.length > 0) {
            imageBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        }

        UpdateClubRequest request = new UpdateClubRequest(name, description, imageBase64, mimeType);
        networkDataSource.updateClub(clubId, request).observeForever(networkResult -> {
            if (networkResult instanceof Result.Success) {
                syncClubById(clubId); // Refresh avatar URL from DB
                result.postValue(new Result.Success<>("Club updated"));
            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                result.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return result;
    }

    @Override
    public LiveData<List<com.grouprace.core.model.ClubStats.LeaderboardEntry>> getLocalLeaderboard(int clubId) {
        return Transformations.map(clubDao.getLeaderboard(clubId), entities -> 
            entities.stream().map(e -> new com.grouprace.core.model.ClubStats.LeaderboardEntry(
                e.memberId, e.memberName, e.avatarUrl, e.distance
            )).collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<Result<String>> syncClubStats(int clubId) {
        MutableLiveData<Result<String>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        networkDataSource.getClubStats(clubId).observeForever(networkResult -> {
            if (networkResult instanceof Result.Success) {
                com.grouprace.core.network.model.club.NetworkClubStats data = ((Result.Success<com.grouprace.core.network.model.club.NetworkClubStats>) networkResult).data;
                
                List<com.grouprace.core.data.model.ClubLeaderboardEntity> leaderboardEntities = data.getLeaderboard() != null 
                    ? data.getLeaderboard().stream().map(n -> new com.grouprace.core.data.model.ClubLeaderboardEntity(
                        clubId, n.getMemberId(), n.getMemberName(), n.getAvatarUrl(), n.getDistance()
                    )).collect(Collectors.toList())
                    : Collections.emptyList();

                Executors.newSingleThreadExecutor().execute(() -> {
                    clubDao.replaceLeaderboardAndStats(
                        clubId,
                        data.getTotalDistance(),
                        data.getTotalActivities(),
                        data.getClubRecordDistanceStr(),
                        data.getClubRecordDurationStr(),
                        data.getPersonalBestDistanceStr(),
                        data.getPersonalBestDurationStr(),
                        leaderboardEntities
                    );
                });
                
                result.postValue(new Result.Success<>("Stats synced"));
            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                result.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return result;
    }

    private ClubEntity toEntity(NetworkClub n) {
        return new ClubEntity(n.getClubId(), n.getName(), n.getDescription(), n.getPrivacyType(), n.getLeaderId(), n.getLeaderName(), n.getMemberCount(), n.getPostCount(), n.getAvatarUrl(), n.getStatus(), 0.0, 0, null, null, null, null);
    }

    private <T> List<T> safeList(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }
}
