package com.grouprace.core.network.source;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.network.api.NotificationApiService;
import com.grouprace.core.network.model.NetworkNotification;
import com.grouprace.core.network.model.NotificationPayload;
import com.grouprace.core.network.model.RegisterDeviceTokenRequest;
import com.grouprace.core.network.utils.ApiResponse;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationNetworkDataSource {

    public final NotificationApiService apiService;

    @Inject
    public NotificationNetworkDataSource(NotificationApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<NetworkNotification>> getNotifications() {
        MutableLiveData<List<NetworkNotification>> liveData = new MutableLiveData<>();

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
                    if (result.isSuccess()) {
                        Log.d("NotificationNetworkDataSource","fetch: " + result.getData().getNotifications());
                        List<NetworkNotification> list = result.getData().getNotifications(); // ← no .getNotifications()
                        Log.d("notifications", " list " + list);
                        liveData.postValue(list);
                    } else {
                        Log.e("NotificationNetworkDataSource", "error result: " + result.getData().getNotifications());
                        liveData.postValue(Collections.emptyList());
                    }
                } else {
                    Log.e("NotificationNetworkDataSource", "HTTP Error: " + response.code() + " " + response.message());
                    liveData.postValue(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<NotificationPayload>> call, Throwable t) {
                Log.e("NotificationNetworkDataSource", "Network Failure" + t.getMessage());
                liveData.postValue(Collections.emptyList());
            }
        });

        return liveData;
    }

    public void registerDeviceToken(int userId, String token) {
        RegisterDeviceTokenRequest body = new RegisterDeviceTokenRequest(userId, token, "android");
        apiService.registerDeviceToken(body).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("DeviceToken", "register success=" + response.body().isSuccess());
                } else {
                    Log.e("DeviceToken", "register HTTP " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e("DeviceToken", "register failure: " + t.getMessage());
            }
        });
    }
}
