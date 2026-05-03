package com.grouprace.core.network.di;

import com.grouprace.core.network.BuildConfig;
import com.grouprace.core.network.api.ClubApiService;
import com.grouprace.core.network.api.DirectionsApiService;
import com.grouprace.core.network.api.FollowApiService;
import com.grouprace.core.network.api.NotificationApiService;
import com.grouprace.core.network.api.SearchApiService;
import com.grouprace.core.network.api.SearchBoxApiService;
import com.grouprace.core.network.api.UserApiService;
import com.grouprace.core.network.utils.AuthInterceptor;
import com.grouprace.core.network.utils.SessionManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.grouprace.core.network.api.UserRouteApiService;
import com.grouprace.core.network.api.PostApiService;
import com.grouprace.core.network.api.AuthApiService;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    private static final String BASE_URL = "http://10.0.2.2:5000/";
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
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL + "/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public PostApiService providePostApiService(Retrofit retrofit) {
        return retrofit.create(PostApiService.class);
    }

    @Provides
    @Singleton
    public RecordApiService provideRecordApiService(Retrofit retrofit) {
        return retrofit.create(RecordApiService.class);
    }

    @Provides
    @Singleton
    public AuthApiService provideAuthService(Retrofit retrofit) {
        return retrofit.create(AuthApiService.class);
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
    public FollowApiService provideFollowApiService(Retrofit retrofit) {
        return retrofit.create(FollowApiService.class);
    }

    @Provides
    @Singleton
    public UserApiService provideUserApiService(Retrofit retrofit) {
        return retrofit.create(UserApiService.class);
    }

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

    @Provides
    @Singleton
    public ClubApiService provideClubApiService(Retrofit retrofit) {
        return retrofit.create(ClubApiService.class);
    }

    @Provides
    @Singleton
    public UserRouteApiService provideUserRouteApiService(Retrofit retrofit) {
        return retrofit.create(UserRouteApiService.class);
    }
}
