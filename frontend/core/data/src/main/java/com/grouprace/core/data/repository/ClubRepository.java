package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.Club;
import com.grouprace.core.model.ClubEvent;
import com.grouprace.core.model.ClubStats;
import com.grouprace.core.model.Post;
import com.grouprace.core.model.Record;

import java.util.List;

public interface ClubRepository {
    LiveData<List<Club>> getLocalMyClubs(int limit);
    LiveData<Club> getLocalClubById(int clubId);
    LiveData<List<Club>> getLocalDiscoverClubs(int limit);
    LiveData<Result<String>> syncClubs(int offset, int limit);
    void syncClubById(int clubId);
    LiveData<Result<String>> joinClub(String clubId);
    LiveData<Result<String>> leaveClub(String clubId);
    LiveData<Result<String>> createClub(String name, String description, String privacyType);

    LiveData<List<com.grouprace.core.model.ClubAdmin>> getAdminsForClub(int clubId);
    LiveData<Result<String>> syncAdmins(int clubId);
    LiveData<Result<Boolean>> checkIsLeader(int clubId);
    LiveData<Result<String>> updateClub(int clubId, String name, String description, byte[] imageBytes, String mimeType);
    
    LiveData<List<ClubStats.LeaderboardEntry>> getLocalLeaderboard(int clubId);
    LiveData<Result<String>> syncClubStats(int clubId);
}
