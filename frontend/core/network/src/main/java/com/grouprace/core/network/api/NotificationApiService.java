package com.grouprace.core.network.api;

import com.grouprace.core.network.model.NotificationPayload;
import com.grouprace.core.network.model.RegisterDeviceTokenRequest;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NotificationApiService {

    @GET("api/notifications")
    Call<ApiResponse<NotificationPayload>> getNotifications();

    @POST("api/device-tokens")
    Call<ApiResponse<Object>> registerDeviceToken(@Body RegisterDeviceTokenRequest body);

    @PUT("api/notifications/{id}/read")
    Call<ApiResponse<Object>> markAsRead(@Path("id") int notificationId);
}
