package com.grouprace.core.network.source;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.network.api.NotificationApiService;
import com.grouprace.core.network.model.notification.NetworkNotification;
import com.grouprace.core.network.model.notification.NotificationPayload;
import com.grouprace.core.network.utils.ApiResponse;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.grouprace.core.common.result.Result;
@Singleton
public class NotificationNetworkDataSource {

    public final NotificationApiService apiService;

    @Inject
    public NotificationNetworkDataSource(NotificationApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Result<List<NetworkNotification>>> getNotifications() {
        MutableLiveData<Result<List<NetworkNotification>>> liveData = new MutableLiveData<>();

        apiService.getNotifications().enqueue(new Callback<ApiResponse<NotificationPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<NotificationPayload>> call, Response<ApiResponse<NotificationPayload>> response) {
                try {
                    String rawJson = response.errorBody() != null
                            ? response.errorBody().string()
                            : "check HttpLoggingInterceptor for success body";
                    Log.d("RAW_RESPONSE", "code: " + response.code());
                    Log.d("RAW_RESPONSE", "body: " + rawJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<NotificationPayload> result = response.body();
                    Log.d("Notification check", "result" + result);
                    if (result.isSuccess() && result.getData() != null) {
                        Log.d("NotificationNetworkDataSource","fetch: " + result.getData().getNotifications());
                        liveData.postValue(new Result.Success<>(result.getData().getNotifications()));
                    } else {
                        Log.e("NotificationNetworkDataSource", "error result: " + result.getData().getNotifications());
                        liveData.postValue(new Result.Error<>(null, result.getMessage()));
                    }
                } else {
                    Log.e("NotificationNetworkDataSource", "HTTP Error: " + response.code() + " " + response.message());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<NotificationPayload>> call, Throwable t) {
                Log.e("NotificationNetworkDataSource", "Network Failure" + t.getMessage());
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<Boolean>> markAsRead(int notificationId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();

        apiService.markAsRead(notificationId).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> result = response.body();

                    if (result.isSuccess()) {
                        Log.d("NotificationNetwork", "markAsRead success: " + notificationId);
                        liveData.postValue(new Result.Success<>(true));
                    } else {
                        Log.e("NotificationNetwork", "markAsRead API error: " + result.getMessage());
                        liveData.postValue(new Result.Error<>(null, result.getMessage()));
                    }
                } else {
                    Log.e("NotificationNetwork", "HTTP Error: " + response.code());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e("NotificationNetwork", "Network Failure: " + t.getMessage());
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }

}
