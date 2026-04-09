package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.NearbyPlace;
import com.grouprace.core.model.PlannedRoute;
import com.grouprace.core.network.api.DirectionsApiService;
import com.grouprace.core.network.api.SearchBoxApiService;
import com.grouprace.core.network.model.directions.DirectionsResponse;
import com.grouprace.core.network.model.searchbox.SearchBoxResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyRouteRepositoryImpl implements NearbyRouteRepository {

    private final SearchBoxApiService searchBoxApi;
    private final DirectionsApiService directionsApi;

    @Inject
    public NearbyRouteRepositoryImpl(SearchBoxApiService searchBoxApi,
                                      DirectionsApiService directionsApi) {
        this.searchBoxApi = searchBoxApi;
        this.directionsApi = directionsApi;
    }

    @Override
    public LiveData<Result<List<NearbyPlace>>> getNearbyPlaces(double lng, double lat,
                                                                String accessToken) {
        MutableLiveData<Result<List<NearbyPlace>>> liveData = new MutableLiveData<>();
        String proximity = String.format(Locale.US, "%.6f,%.6f", lng, lat);

        searchBoxApi.getNearbyPlaces("park,trail,landmark", proximity, 10, accessToken)
                .enqueue(new Callback<SearchBoxResponse>() {
                    @Override
                    public void onResponse(Call<SearchBoxResponse> call,
                                           Response<SearchBoxResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().features != null) {
                            liveData.postValue(new Result.Success<>(
                                    parseFeatures(response.body().features)));
                        } else {
                            liveData.postValue(new Result.Error<>(null, "No places found nearby"));
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchBoxResponse> call, Throwable t) {
                        liveData.postValue(new Result.Error<>(
                                new Exception(t), "Failed to load nearby places"));
                    }
                });

        return liveData;
    }

    @Override
    public LiveData<Result<List<NearbyPlace>>> searchByQuery(String query, double lng, double lat,
                                                              String accessToken) {
        MutableLiveData<Result<List<NearbyPlace>>> liveData = new MutableLiveData<>();
        String proximity = String.format(Locale.US, "%.6f,%.6f", lng, lat);

        searchBoxApi.searchByText(query, proximity, true, 5, accessToken)
                .enqueue(new Callback<SearchBoxResponse>() {
                    @Override
                    public void onResponse(Call<SearchBoxResponse> call,
                                           Response<SearchBoxResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().features != null) {
                            List<NearbyPlace> places = parseFeatures(response.body().features);
                            if (places.isEmpty()) {
                                liveData.postValue(new Result.Error<>(null, "No places found"));
                            } else {
                                liveData.postValue(new Result.Success<>(places));
                            }
                        } else {
                            liveData.postValue(new Result.Error<>(null, "No places found"));
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchBoxResponse> call, Throwable t) {
                        liveData.postValue(new Result.Error<>(
                                new Exception(t), "Search failed"));
                    }
                });

        return liveData;
    }

    private List<NearbyPlace> parseFeatures(List<SearchBoxResponse.Feature> features) {
        List<NearbyPlace> places = new ArrayList<>();
        for (SearchBoxResponse.Feature f : features) {
            if (f.properties == null || f.geometry == null) continue;
            if (f.geometry.coordinates == null || f.geometry.coordinates.size() < 2) continue;
            places.add(new NearbyPlace(
                    f.properties.name,
                    f.geometry.coordinates.get(0),
                    f.geometry.coordinates.get(1),
                    f.properties.distance
            ));
        }
        return places;
    }

    @Override
    public LiveData<Result<PlannedRoute>> generateRoute(double userLng, double userLat,
                                                         double placeLng, double placeLat,
                                                         String accessToken) {
        MutableLiveData<Result<PlannedRoute>> liveData = new MutableLiveData<>();

        // Round-trip: user → place → user
        String coords = String.format(Locale.US,
                "%.6f,%.6f;%.6f,%.6f;%.6f,%.6f",
                userLng, userLat, placeLng, placeLat, userLng, userLat);

        directionsApi.getRoute(coords, "geojson", accessToken)
                .enqueue(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call,
                                           Response<DirectionsResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().routes != null
                                && !response.body().routes.isEmpty()) {

                            DirectionsResponse.Route route = response.body().routes.get(0);
                            List<double[]> coordinates = new ArrayList<>();

                            if (route.geometry != null && route.geometry.coordinates != null) {
                                for (List<Double> c : route.geometry.coordinates) {
                                    if (c.size() >= 2) {
                                        coordinates.add(new double[]{c.get(0), c.get(1)});
                                    }
                                }
                            }

                            double distanceKm = route.distance / 1000.0;
                            int durationSeconds = (int) route.duration;

                            liveData.postValue(new Result.Success<>(
                                    new PlannedRoute(coordinates, distanceKm, durationSeconds)));
                        } else {
                            liveData.postValue(new Result.Error<>(null, "Could not generate route"));
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        liveData.postValue(new Result.Error<>(
                                new Exception(t), "Failed to generate route"));
                    }
                });

        return liveData;
    }
}
