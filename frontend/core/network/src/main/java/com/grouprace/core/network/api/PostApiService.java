package com.grouprace.core.network.api;

import com.grouprace.core.network.model.NetworkPost;
import com.grouprace.core.network.model.PostPayload;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface PostApiService {
    
    @GET("api/posts/feed")
    Call<ApiResponse<PostPayload>> getPosts();
}
