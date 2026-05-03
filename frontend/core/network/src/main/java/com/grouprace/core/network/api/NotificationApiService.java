package com.grouprace.core.network.api;

import com.grouprace.core.network.model.notification.NotificationPayload;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NotificationApiService {

    @GET("api/notifications")
    Call<ApiResponse<NotificationPayload>> getNotifications(
            @retrofit2.http.Query("cursor") Integer cursor,
            @retrofit2.http.Query("limit") int limit
    );
    @PUT("api/notifications/{id}/read")
    Call<ApiResponse<Object>> markAsRead(@Path("id") int notificationId);
}
