package com.grouprace.core.network.di;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;
import com.grouprace.core.network.api.DirectionsApiService;
import com.grouprace.core.network.api.NotificationApiService;
import com.grouprace.core.network.api.RecordApiService;
import com.grouprace.core.network.api.SearchBoxApiService;
import com.grouprace.core.network.api.UserApiService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.grouprace.core.network.api.SearchApiService;
import com.grouprace.core.network.utils.AuthInterceptor;
import com.grouprace.core.network.utils.SessionManager;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    private static final String BASE_URL = "http://192.168.110.19:5000/";

    @Provides
    @Singleton
    public HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    @Provides
    @Singleton
    public AuthInterceptor provideAuthInterceptor(SessionManager sessionManager) {
        return new AuthInterceptor(sessionManager);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(HttpLoggingInterceptor loggingInterceptor,
            AuthInterceptor authInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    @Provides
    @Singleton
    public com.grouprace.core.network.api.PostApiService providePostApiService(Retrofit retrofit) {
        return retrofit.create(com.grouprace.core.network.api.PostApiService.class);
    }

    @Provides
    @Singleton
    public com.grouprace.core.network.api.RecordApiService provideRecordApiService(Retrofit retrofit) {
        return retrofit.create(com.grouprace.core.network.api.RecordApiService.class);
    }

    @Provides
    @Singleton
    public com.grouprace.core.network.api.AuthApiService provideAuthService(Retrofit retrofit) {
        return retrofit.create(com.grouprace.core.network.api.AuthApiService.class);
    }

    @Provides
    @Singleton
    public NotificationApiService provideNotificationApiService(Retrofit retrofit) {
        return retrofit.create(NotificationApiService.class);
    }

    @Provides
    @Singleton
    public SearchApiService provideSearchApiService(Retrofit retrofit) {
        return retrofit.create(SearchApiService.class);
    }

    @Provides
    @Singleton
    public UserApiService provideUserApiService(Retrofit retrofit) {
        return retrofit.create(UserApiService.class);
    }

    // Mapbox REST APIs (no auth interceptor — token passed as query param)
    @Provides
    @Singleton
    @Named("mapbox")
    public Retrofit provideMapboxRetrofit(HttpLoggingInterceptor loggingInterceptor) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder()
                .baseUrl("https://api.mapbox.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public SearchBoxApiService provideSearchBoxApiService(@Named("mapbox") Retrofit retrofit) {
        return retrofit.create(SearchBoxApiService.class);
    }

    @Provides
    @Singleton
    public DirectionsApiService provideDirectionsApiService(@Named("mapbox") Retrofit retrofit) {
        return retrofit.create(DirectionsApiService.class);
    }
}
