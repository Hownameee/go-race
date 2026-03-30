package com.grouprace.core.network.source;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.PostApiService;
import com.grouprace.core.network.model.post.NetworkPost;
import com.grouprace.core.network.model.post.PostPayload;
import com.grouprace.core.network.model.post.CreateCommentRequest;
import com.grouprace.core.network.model.post.CommentPayload;
import com.grouprace.core.network.utils.ApiResponse;

import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

import java.util.List;

public class PostNetworkDataSource {

    private final PostApiService apiService;

    @Inject
    public PostNetworkDataSource(PostApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Result<List<NetworkPost>>> getPosts(String cursor, int limit) {
        MutableLiveData<Result<List<NetworkPost>>> liveData = new MutableLiveData<>();
        
        liveData.postValue(new Result.Loading<>());
        
        apiService.getPosts(cursor, limit).enqueue(new Callback<ApiResponse<PostPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostPayload>> call, Response<ApiResponse<PostPayload>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PostPayload> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d("PostNetworkDataSource", "Successfully fetched " + apiResponse.getData().getPosts().size() + " posts");
                        liveData.postValue(new Result.Success<>(apiResponse.getData().getPosts()));
                    } else {
                        Log.e("PostNetworkDataSource", "API returned success false or null data. Message: " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(new Exception(apiResponse.getMessage()), apiResponse.getMessage()));
                    }
                } else {
                    Log.e("PostNetworkDataSource", "HTTP Error: " + response.code() + " " + response.message());
                    String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                    liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostPayload>> call, Throwable t) {
                Log.e("PostNetworkDataSource", "Network Failure: " + t.getMessage(), t);
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });
        
        return liveData;
    }

    public LiveData<Result<Boolean>> likePost(int postId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.likePost(postId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("PostNetworkDataSource", "Post " + postId + " liked successfully");
                    liveData.postValue(new Result.Success<>(true));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    Log.e("PostNetworkDataSource", "Like failed: " + msg);
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("PostNetworkDataSource", "Like network failure: " + t.getMessage(), t);
                Exception ex = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(ex, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<Boolean>> unlikePost(int postId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.unlikePost(postId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("PostNetworkDataSource", "Post " + postId + " unliked successfully");
                    liveData.postValue(new Result.Success<>(true));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    Log.e("PostNetworkDataSource", "Unlike failed: " + msg);
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("PostNetworkDataSource", "Unlike network failure: " + t.getMessage(), t);
                Exception ex = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(ex, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }
    public LiveData<Result<CommentPayload>> getComments(int postId) {
        MutableLiveData<Result<CommentPayload>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getComments(postId).enqueue(new Callback<ApiResponse<CommentPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<CommentPayload>> call, Response<ApiResponse<CommentPayload>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("PostNetworkDataSource", "Successfully fetched " + response.body().getData().getComments().size() + " comments"); 
                    liveData.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    Log.e("PostNetworkDataSource", "Get comments failed: " + msg);
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CommentPayload>> call, Throwable t) {
                Log.e("PostNetworkDataSource", "Get comments network failure: " + t.getMessage(), t);
                Exception ex = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(ex, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<Boolean>> createComment(int postId, String content) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        CreateCommentRequest request = new CreateCommentRequest(content);

        apiService.createComment(postId, request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("PostNetworkDataSource", "Comment created successfully");
                    liveData.postValue(new Result.Success<>(true));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    Log.e("PostNetworkDataSource", "Create comment failed: " + msg);
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("PostNetworkDataSource", "Create comment network failure: " + t.getMessage(), t);
                Exception ex = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(ex, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<Boolean>> deleteComment(int postId, int commentId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.deleteComment(postId, commentId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("PostNetworkDataSource", "Comment deleted successfully");
                    liveData.postValue(new Result.Success<>(true));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    Log.e("PostNetworkDataSource", "Delete comment failed: " + msg);
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("PostNetworkDataSource", "Delete comment network failure: " + t.getMessage(), t);
                Exception ex = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(ex, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }
}
