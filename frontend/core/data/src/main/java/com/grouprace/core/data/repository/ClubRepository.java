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
    
    LiveData<List<Club>> getLocalDiscoverClubs(int limit);
    
    LiveData<Result<String>> syncClubs(int offset, int limit);

    
    LiveData<Result<Club>> getClubDetails(String clubId);
    
    LiveData<Result<List<Post>>> getClubPosts(String clubId);
    
    LiveData<Result<List<Record>>> getClubActivities(String clubId);
    
    LiveData<Result<List<ClubEvent>>> getClubEvents(String clubId);
    
    LiveData<Result<ClubStats>> getClubStats(String clubId);
    
    LiveData<Result<String>> joinClub(String clubId);
    
    LiveData<Result<Boolean>> leaveClub(String clubId);

    LiveData<Result<Boolean>> deleteClub(String clubId);

    LiveData<Result<String>> createClub(String name, String description, String privacyType);
}
