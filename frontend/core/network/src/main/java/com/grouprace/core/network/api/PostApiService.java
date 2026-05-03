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

    @GET("api/posts/me")
    Call<ApiResponse<PostPayload>> getMyPosts(@Query("cursor") String cursor, @Query("limit") int limit);

    // ===== Profile Section ====
    @GET("api/posts/users/{userId}")
    Call<ApiResponse<PostPayload>> getUserPosts(
            @Path("userId") int userId,
            @Query("cursor") String cursor,
            @Query("limit") int limit
    );

    @retrofit2.http.Multipart
    @POST("api/posts")
    Call<ApiResponse<Void>> createPost(
        @retrofit2.http.PartMap java.util.Map<String, okhttp3.RequestBody> params,
        @retrofit2.http.Part java.util.List<okhttp3.MultipartBody.Part> photos
    );

    @POST("api/posts/{postId}/like")
    Call<ApiResponse<Void>> likePost(@Path("postId") int postId);

    @DELETE("api/posts/{postId}/like")
    Call<ApiResponse<Void>> unlikePost(@Path("postId") int postId);

    @GET("api/posts/{postId}/comments")
    Call<ApiResponse<CommentPayload>> getComments(
        @Path("postId") int postId,
        @Query("cursor") String cursor,
        @Query("limit") int limit
    );

    @POST("api/posts/{postId}/comments/{commentId}/like")
    Call<ApiResponse<Void>> likeComment(
        @Path("postId") int postId,
        @Path("commentId") int commentId
    );

    @DELETE("api/posts/{postId}/comments/{commentId}/like")
    Call<ApiResponse<Void>> unlikeComment(
        @Path("postId") int postId,
        @Path("commentId") int commentId
    );

    @GET("api/posts/{postId}/comments/{commentId}/replies")
    Call<ApiResponse<CommentPayload>> getReplies(
        @Path("postId") int postId,
        @Path("commentId") int commentId,
        @Query("cursor") String cursor,
        @Query("limit") int limit
    );
    
    @POST("api/posts/{postId}/comments")
    Call<ApiResponse<Void>> createComment(
        @Path("postId") int postId, 
        @Body CreateCommentRequest request
    );

    @DELETE("api/posts/{postId}/comments/{commentId}")
    Call<ApiResponse<Void>> deleteComment(
        @Path("postId") int postId,
        @Path("commentId") int commentId
    );

    @GET("api/clubs/{clubId}/posts")
    Call<ApiResponse<PostPayload>> getClubPosts(
        @Path("clubId") int clubId,
        @Query("cursor") String cursor, 
        @Query("limit") int limit
    );
}
