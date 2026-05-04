package com.grouprace.core.network.api;

import com.grouprace.core.network.model.search.ClubActionResultResponse;
import com.grouprace.core.network.model.search.NetworkUserSearch;
import com.grouprace.core.network.utils.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SearchApiService {

    @GET("api/users/search")
    Call<ApiResponse<List<NetworkUserSearch>>> searchUsers(@Query("search") String query);

    @GET("api/users/suggest")
    Call<ApiResponse<List<NetworkUserSearch>>> getSuggestedUsers();

    @GET("api/clubs/search")
    Call<ApiResponse<List<NetworkUserSearch>>> searchClubs(@Query("query") String query);

    @GET("api/clubs/suggest")
    Call<ApiResponse<List<NetworkUserSearch>>> getSuggestedClubs();
    /**
     * Tham gia Câu lạc bộ.
     */
    @POST("api/clubs/{clubId}/join")
    Call<ApiResponse<ClubActionResultResponse>> joinClub(@Path("clubId") int clubId);

    /**
     * Rời Câu lạc bộ.
     */
    @POST("api/clubs/{clubId}/leave")
    Call<ApiResponse<ClubActionResultResponse>> leaveClub(@Path("clubId") int clubId);

}
