package com.grouprace.core.network.source;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.network.api.PostApiService;
import com.grouprace.core.network.model.NetworkPost;
import com.grouprace.core.network.model.PostPayload;
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

    public LiveData<List<NetworkPost>> getPosts() {
        MutableLiveData<List<NetworkPost>> liveData = new MutableLiveData<>();
        
        apiService.getPosts().enqueue(new Callback<ApiResponse<PostPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostPayload>> call, Response<ApiResponse<PostPayload>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PostPayload> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d("PostNetworkDataSource", "Successfully fetched " + apiResponse.getData().getPosts().size() + " posts");
                        liveData.postValue(apiResponse.getData().getPosts());
                    } else {
                        Log.e("PostNetworkDataSource", "API returned success false or null data. Message: " + apiResponse.getMessage());
                        liveData.postValue(null);
                    }
                } else {
                    Log.e("PostNetworkDataSource", "HTTP Error: " + response.code() + " " + response.message());
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostPayload>> call, Throwable t) {
                Log.e("PostNetworkDataSource", "Network Failure: " + t.getMessage(), t);
                liveData.postValue(null);
            }
        });
        
        return liveData;
    }
}
