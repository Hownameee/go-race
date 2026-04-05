package com.grouprace.core.network.api;

import com.grouprace.core.network.model.search.NetworkUserSearch;
import com.grouprace.core.network.utils.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SearchApiService {

    // --- USER ENDPOINTS ---

    /**
     * Tìm kiếm người dùng theo từ khóa.
     * Endpoint: GET /api/users/search?query=...
     */
    @GET("api/users/search")
    Call<ApiResponse<List<NetworkUserSearch>>> searchUsers(@Query("search") String query);

    /**
     * Lấy danh sách người dùng gợi ý (People You May Know).
     * Endpoint: GET /api/users/suggest
     */
    @GET("api/users/suggest")
    Call<ApiResponse<List<NetworkUserSearch>>> getSuggestedUsers();

    /**
     * Theo dõi (Follow) một người dùng.
     */
    @POST("api/users/{followingId}/follow")
    Call<ApiResponse<Void>> followUser(@Path("followingId") int targetUserId);

    /**
     * Hủy theo dõi (Unfollow) một người dùng.
     */
    @DELETE("api/users/{followingId}/follow")
    Call<ApiResponse<Void>> unfollowUser(@Path("followingId") int targetUserId);


    // --- CLUB ENDPOINTS (Thêm mới) ---

    /**
     * Tìm kiếm Câu lạc bộ theo từ khóa.
     * Endpoint: GET /api/clubs/search?query=...
     */
    @GET("api/clubs/search")
    Call<ApiResponse<List<NetworkUserSearch>>> searchClubs(@Query("query") String query);

    /**
     * Lấy danh sách Câu lạc bộ gợi ý (Popular/Local Clubs).
     * Endpoint: GET /api/clubs/suggest
     */
    @GET("api/clubs/suggest")
    Call<ApiResponse<List<NetworkUserSearch>>> getSuggestedClubs();

}