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
import com.grouprace.core.model.ClubMember;
import com.grouprace.core.network.model.club.ClubListPayload;
import com.grouprace.core.network.model.club.ClubPayload;
import com.grouprace.core.network.model.club.CreateClubRequest;
import com.grouprace.core.network.model.club.JoinClubResponse;
import com.grouprace.core.network.model.club.NetworkClub;
import com.grouprace.core.network.model.club.NetworkClubAdmin;
import com.grouprace.core.network.model.club.UpdateClubRequest;
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
    private final ClubAdminDao clubAdminDao;
    private final com.grouprace.core.data.dao.EventDao eventDao;

    @Inject
    public ClubRepositoryImpl(ClubNetworkDataSource networkDataSource, ClubDao clubDao, ClubAdminDao clubAdminDao, com.grouprace.core.data.dao.EventDao eventDao) {
        this.networkDataSource = networkDataSource;
        this.clubDao = clubDao;
        this.clubAdminDao = clubAdminDao;
        this.eventDao = eventDao;
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
                    clubDao.updateStatus(id, "left");
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
    public LiveData<Result<Boolean>> checkIsAdmin(int clubId) {
        return networkDataSource.checkIsAdmin(clubId);
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
    public LiveData<Result<com.grouprace.core.model.ClubStats>> syncClubStats(int clubId) {
        MutableLiveData<Result<com.grouprace.core.model.ClubStats>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        networkDataSource.getClubStats(clubId).observeForever(networkResult -> {
            if (networkResult instanceof Result.Success) {
                com.grouprace.core.network.model.club.NetworkClubStats data = ((Result.Success<com.grouprace.core.network.model.club.NetworkClubStats>) networkResult).data;
                
                Executors.newSingleThreadExecutor().execute(() -> {
                    // Update only the basic stats in ClubEntity, NOT the leaderboard table
                    clubDao.replaceLeaderboardAndStats(
                        clubId,
                        data.getTotalDistance(),
                        data.getTotalActivities(),
                        data.getClubRecordDistanceStr(),
                        data.getClubRecordDurationStr(),
                        data.getPersonalBestDistanceStr(),
                        data.getPersonalBestDurationStr()
                    );
                });
                
                // Map to Domain Model
                List<com.grouprace.core.model.ClubStats.LeaderboardEntry> leaderboard = data.getLeaderboard() != null
                    ? data.getLeaderboard().stream().map(n -> new com.grouprace.core.model.ClubStats.LeaderboardEntry(
                        n.getMemberId(), n.getMemberName(), n.getAvatarUrl(), n.getDistance()
                    )).collect(Collectors.toList())
                    : Collections.emptyList();

                com.grouprace.core.model.ClubStats domainStats = new com.grouprace.core.model.ClubStats(
                    data.getTotalDistance(),
                    data.getTotalActivities(),
                    data.getClubRecordDistanceStr(),
                    data.getClubRecordDurationStr(),
                    data.getPersonalBestDistanceStr(),
                    data.getPersonalBestDurationStr(),
                    leaderboard
                );

                result.postValue(new Result.Success<>(domainStats));
            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                result.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return result;
    }

    @Override
    public LiveData<List<com.grouprace.core.model.ClubEvent>> getLocalEvents(int clubId) {
        return Transformations.map(eventDao.getEventsByClubId(clubId), entities -> 
            entities.stream().map(e -> new com.grouprace.core.model.ClubEvent(
                e.eventId, e.clubId, e.title, e.description, e.targetDistance, e.targetDurationSeconds, e.startTime, e.endTime, e.isJoined, e.currentDistance, e.currentDurationSeconds, e.participantsCount, e.globalDistance, e.globalDurationSeconds
            )).collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<Result<String>> syncEvents(int clubId) {
        MutableLiveData<Result<String>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        networkDataSource.getEvents(clubId).observeForever(networkResult -> {
            if (networkResult instanceof Result.Success) {
                List<com.grouprace.core.network.model.club.NetworkClubEvent> data = ((Result.Success<List<com.grouprace.core.network.model.club.NetworkClubEvent>>) networkResult).data;
                
                List<com.grouprace.core.data.model.EventEntity> entities = data.stream().map(n -> 
                    new com.grouprace.core.data.model.EventEntity(
                        n.eventId, n.clubId, n.title, n.description, n.targetDistance, n.targetDurationSeconds, n.startTime, n.endTime, n.isJoined == 1, n.currentDistance, n.currentDurationSeconds, n.participantsCount, n.globalDistance, n.globalDurationSeconds
                    )
                ).collect(Collectors.toList());

                Executors.newSingleThreadExecutor().execute(() -> {
                    eventDao.replaceEventsForClub(clubId, entities);
                });
                
                result.postValue(new Result.Success<>("Events synced"));
            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                result.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return result;
    }

    @Override
    public LiveData<Result<String>> createEvent(int clubId, String title, String description, double targetDistance, int targetDurationSeconds, String startTime, String endTime) {
        com.grouprace.core.network.model.club.CreateClubEventRequest request = new com.grouprace.core.network.model.club.CreateClubEventRequest(title, description, targetDistance, targetDurationSeconds, startTime, endTime);
        return networkDataSource.createEvent(clubId, request);
    }

    @Override
    public LiveData<Result<String>> joinEvent(int clubId, int eventId) {
        return networkDataSource.joinEvent(clubId, eventId);
    }

    @Override
    public LiveData<Result<com.grouprace.core.model.EventStats>> syncEventStats(int clubId, int eventId) {
        MutableLiveData<Result<com.grouprace.core.model.EventStats>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        networkDataSource.getEventStats(clubId, eventId).observeForever(networkResult -> {
            if (networkResult instanceof Result.Success) {
                com.grouprace.core.network.model.club.NetworkEventStats data = ((Result.Success<com.grouprace.core.network.model.club.NetworkEventStats>) networkResult).data;
                
                // Update local database (Room) for offline-first summary
                new Thread(() -> {
                    com.grouprace.core.data.model.EventEntity existing = eventDao.getEventByIdSync(data.eventId);
                    boolean isJoined = existing != null && existing.isJoined;
                    double currentDistance = existing != null ? existing.currentDistance : 0.0;
                    int currentDuration = existing != null ? existing.currentDurationSeconds : 0;

                    com.grouprace.core.data.model.EventEntity entity = new com.grouprace.core.data.model.EventEntity(
                        data.eventId, data.clubId, data.title, data.description, 
                        data.targetDistance, data.targetDurationSeconds, 
                        data.startTime, data.endTime, 
                        isJoined, currentDistance, currentDuration, 
                        data.participantsCount, data.totalDistance, data.totalDurationSeconds
                    );
                    eventDao.insertEvent(entity);
                }).start();

                List<com.grouprace.core.model.ClubStats.LeaderboardEntry> leaderboard = data.leaderboard != null
                    ? data.leaderboard.stream().map(n -> new com.grouprace.core.model.ClubStats.LeaderboardEntry(
                        n.memberId, n.memberName, n.avatarUrl, n.distance
                    )).collect(Collectors.toList())
                    : Collections.emptyList();

                com.grouprace.core.model.EventStats domainStats = new com.grouprace.core.model.EventStats(
                    data.eventId,
                    data.clubId,
                    data.title,
                    data.description,
                    data.targetDistance,
                    data.targetDurationSeconds,
                    data.startTime,
                    data.endTime,
                    data.participantsCount,
                    data.totalDistance,
                    data.totalDurationSeconds,
                    leaderboard
                );

                result.postValue(new Result.Success<>(domainStats));
            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                result.postValue(new Result.Error<>(error.exception, error.message));
            }
        });

        return result;
    }

    @Override
    public LiveData<Result<List<ClubMember>>> getMembers(int clubId) {
        return Transformations.map(networkDataSource.getMembers(clubId), networkResult -> {
            if (networkResult instanceof Result.Success) {
                List<com.grouprace.core.network.model.club.NetworkClubMember> data = ((Result.Success<List<com.grouprace.core.network.model.club.NetworkClubMember>>) networkResult).data;
                List<ClubMember> members = data.stream().map(n -> new ClubMember(
                    n.getUserId(), n.getFullname(), n.getAvatarUrl(), n.getRole(), n.getStatus(), n.getJoinedAt(), n.isLeader()
                )).collect(Collectors.toList());
                return new Result.Success<>(members);
            } else if (networkResult instanceof Result.Error) {
                Result.Error<?> error = (Result.Error<?>) networkResult;
                return new Result.Error<>(error.exception, error.message);
            } else {
                return new Result.Loading<>();
            }
        });
    }

    @Override
    public LiveData<Result<String>> updateMemberStatus(int clubId, int userId, String status) {
        return networkDataSource.updateMemberStatus(clubId, userId, status);
    }

    @Override
    public LiveData<Result<String>> updateMemberRole(int clubId, int userId, String role) {
        return networkDataSource.updateMemberRole(clubId, userId, role);
    }

    private ClubEntity toEntity(NetworkClub n) {
        return new ClubEntity(n.getClubId(), n.getName(), n.getDescription(), n.getPrivacyType(), n.getLeaderId(), n.getLeaderName(), n.getMemberCount(), n.getPostCount(), n.getAvatarUrl(), n.getStatus(), 0.0, 0, null, null, null, null);
    }

    private <T> List<T> safeList(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }
}
