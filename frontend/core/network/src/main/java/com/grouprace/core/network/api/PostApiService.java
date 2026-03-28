package com.grouprace.core.network.api;

import com.grouprace.core.network.model.post.CreateCommentRequest;
import com.grouprace.core.network.model.post.CommentPayload;
import com.grouprace.core.network.model.post.CreatePostRequest;
import com.grouprace.core.network.model.post.PostPayload;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostApiService {

    @GET("api/posts/feed")
    Call<ApiResponse<PostPayload>> getPosts(@Query("cursor") String cursor, @Query("limit") int limit);

    @POST("api/posts")
    Call<ApiResponse<Void>> createPost(@Body CreatePostRequest request);

    @POST("api/posts/{postId}/like")
    Call<ApiResponse<Void>> likePost(@Path("postId") int postId);

    @DELETE("api/posts/{postId}/like")
    Call<ApiResponse<Void>> unlikePost(@Path("postId") int postId);

    @GET("api/posts/{postId}/comments")
    Call<ApiResponse<CommentPayload>> getComments(@Path("postId") int postId);
    
    @POST("api/posts/{postId}/comments")
    Call<ApiResponse<Void>> createComment(
        @Path("postId") int postId, 
        @Body CreateCommentRequest request
    );

    @DELETE("api/posts/{postId}/comment/{commentId}")
    Call<ApiResponse<Void>> deleteComment(
        @Path("postId") int postId,
        @Path("commentId") int commentId
    );
}
