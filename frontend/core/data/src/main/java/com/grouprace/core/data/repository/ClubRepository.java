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
    LiveData<Result<Boolean>> checkIsAdmin(int clubId);
    LiveData<Result<String>> updateClub(int clubId, String name, String description, byte[] imageBytes, String mimeType);
    
    LiveData<Result<com.grouprace.core.model.ClubStats>> syncClubStats(int clubId);

    LiveData<List<ClubEvent>> getLocalEvents(int clubId);
    LiveData<Result<String>> syncEvents(int clubId);
    LiveData<Result<String>> createEvent(int clubId, String title, String description, double targetDistance, String startTime, String endTime);
    LiveData<Result<String>> joinEvent(int clubId, int eventId);
    LiveData<Result<com.grouprace.core.model.EventStats>> syncEventStats(int clubId, int eventId);

    LiveData<Result<List<com.grouprace.core.model.ClubMember>>> getMembers(int clubId);
    LiveData<Result<String>> updateMemberStatus(int clubId, int userId, String status);
    LiveData<Result<String>> updateMemberRole(int clubId, int userId, String role);
}
