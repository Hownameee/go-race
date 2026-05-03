package com.grouprace.core.network.source;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.PostApiService;
import com.grouprace.core.network.model.post.NetworkPost;
import com.grouprace.core.network.model.post.PostPayload;
import com.grouprace.core.network.model.post.CreateCommentRequest;
import com.grouprace.core.network.model.post.CreatePostRequest;
import com.grouprace.core.network.model.post.CommentPayload;
import com.grouprace.core.network.utils.ApiResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

import java.util.List;

public class PostNetworkDataSource {

    private final PostApiService apiService;
    private final android.content.Context context;

    @Inject
    public PostNetworkDataSource(PostApiService apiService, @dagger.hilt.android.qualifiers.ApplicationContext android.content.Context context) {
        this.apiService = apiService;
        this.context = context;
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

    public LiveData<Result<List<NetworkPost>>> getMyPosts(String cursor, int limit) {
        MutableLiveData<Result<List<NetworkPost>>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getMyPosts(cursor, limit).enqueue(new Callback<ApiResponse<PostPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostPayload>> call, Response<ApiResponse<PostPayload>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PostPayload> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData().getPosts()));
                    } else {
                        String msg = apiResponse.getMessage() != null
                                ? apiResponse.getMessage()
                                : "Load my posts failed.";
                        liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                    }
                } else {
                    String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                    liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostPayload>> call, Throwable t) {
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    // ===== Profile Section ====
    public LiveData<Result<List<NetworkPost>>> getUserPosts(int userId, String cursor, int limit) {
        MutableLiveData<Result<List<NetworkPost>>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getUserPosts(userId, cursor, limit).enqueue(new Callback<ApiResponse<PostPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostPayload>> call, Response<ApiResponse<PostPayload>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PostPayload> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData().getPosts()));
                    } else {
                        String msg = apiResponse.getMessage() != null
                                ? apiResponse.getMessage()
                                : "Load user posts failed.";
                        liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                    }
                } else {
                    String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                    liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostPayload>> call, Throwable t) {
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
    public LiveData<Result<CommentPayload>> getComments(int postId, String cursor, int limit) {
        MutableLiveData<Result<CommentPayload>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getComments(postId, cursor, limit).enqueue(new Callback<ApiResponse<CommentPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<CommentPayload>> call, Response<ApiResponse<CommentPayload>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("PostNetworkDataSource", "Successfully fetched comments"); 
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

    public LiveData<Result<Boolean>> likeComment(int postId, int commentId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.likeComment(postId, commentId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Result.Success<>(true));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Exception ex = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(ex, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<Boolean>> unlikeComment(int postId, int commentId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.unlikeComment(postId, commentId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Result.Success<>(true));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Exception ex = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(ex, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<CommentPayload>> getReplies(int postId, int commentId, String cursor, int limit) {
        MutableLiveData<Result<CommentPayload>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getReplies(postId, commentId, cursor, limit).enqueue(new Callback<ApiResponse<CommentPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<CommentPayload>> call, Response<ApiResponse<CommentPayload>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CommentPayload>> call, Throwable t) {
                Exception ex = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(ex, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<Boolean>> createComment(int postId, String content, Integer parentId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        CreateCommentRequest request = new CreateCommentRequest(content, parentId);

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
    public LiveData<Result<Boolean>> createPost(
            CreatePostRequest request,
            List<String> photoUris
    ) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        List<MultipartBody.Part> photos = resolvePhotoParts(photoUris);
        apiService.createPost(request.toPartMap(), photos).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("PostNetworkDataSource", "Post created successfully");
                    liveData.postValue(new Result.Success<>(true));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    Log.e("PostNetworkDataSource", "Create post failed: " + msg);
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("PostNetworkDataSource", "Create post network failure: " + t.getMessage(), t);
                Exception ex = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(ex, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public Result<Boolean> createPostSync(
            CreatePostRequest request,
            List<String> photoUris
    ) {
        try {
            List<MultipartBody.Part> photos = resolvePhotoParts(photoUris);
            Response<ApiResponse<Void>> response = apiService.createPost(request.toPartMap(), photos).execute();
            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                return new Result.Success<>(true);
            } else {
                String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                return new Result.Error<>(new Exception(msg), msg);
            }
        } catch (java.io.IOException e) {
            return new Result.Error<>(e, e.getMessage());
        }
    }

    private List<MultipartBody.Part> resolvePhotoParts(List<String> photoUris) {
        List<MultipartBody.Part> photoParts = new java.util.ArrayList<>();
        if (photoUris != null) {
            for (String uriString : photoUris) {
                try {
                    android.net.Uri uri = android.net.Uri.parse(uriString);
                    String mimeType = context.getContentResolver().getType(uri);
                    if (mimeType == null) mimeType = "image/jpeg";

                    java.io.InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    if (inputStream != null) {
                        byte[] bytes = readAllBytes(inputStream);
                        RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), bytes);
                        photoParts.add(MultipartBody.Part.createFormData("photos", "photo_" + System.currentTimeMillis() + ".jpg", requestBody));
                    }
                } catch (Exception e) {
                    Log.e("PostNetworkDataSource", "Failed to read image URI: " + uriString, e);
                }
            }
        }
        return photoParts;
    }

    private byte[] readAllBytes(java.io.InputStream inputStream) throws java.io.IOException {
        java.io.ByteArrayOutputStream byteBuffer = new java.io.ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public LiveData<Result<List<NetworkPost>>> getClubPosts(int clubId, String cursor, int limit) {
        MutableLiveData<Result<List<NetworkPost>>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getClubPosts(clubId, cursor, limit).enqueue(new Callback<ApiResponse<PostPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostPayload>> call, Response<ApiResponse<PostPayload>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PostPayload> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData().getPosts()));
                    } else {
                        liveData.postValue(new Result.Error<>(new Exception(apiResponse.getMessage()), apiResponse.getMessage()));
                    }
                } else {
                    String errorMessage = "HTTP Error: " + response.code();
                    liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostPayload>> call, Throwable t) {
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }
}
