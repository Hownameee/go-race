package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.UserSearchResult;

import java.util.List;

public interface SearchRepository {

    // --- USER / FRIEND METHODS ---

    /**
     * Tìm kiếm người dùng theo tên.
     */
    LiveData<Result<List<UserSearchResult>>> searchUsers(String targetName);

    /**
     * Lấy danh sách gợi ý người dùng (People You May Know).
     */
    LiveData<Result<List<UserSearchResult>>> getSuggestedUsers();


    // --- CLUB METHODS ---

    /**
     * Tìm kiếm câu lạc bộ theo tên.
     */
    LiveData<Result<List<UserSearchResult>>> searchClubs(String targetName);

    /**
     * Lấy danh sách câu lạc bộ gợi ý (Popular/Local Clubs).
     */
    LiveData<Result<List<UserSearchResult>>> getSuggestedClubs();

    LiveData<Result<String>> joinClub(int clubId);

    LiveData<Result<String>> leaveClub(int clubId);
}