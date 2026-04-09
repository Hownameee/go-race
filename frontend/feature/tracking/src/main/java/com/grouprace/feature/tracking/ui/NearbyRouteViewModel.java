package com.grouprace.feature.tracking.ui;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.NearbyRouteRepository;
import com.grouprace.core.model.NearbyPlace;
import com.grouprace.core.model.PlannedRoute;
import com.mapbox.common.MapboxOptions;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NearbyRouteViewModel extends ViewModel {

    public enum UiState { IDLE, LOADING, ROUTE_READY }

    private final NearbyRouteRepository repository;

    private final MutableLiveData<UiState> uiState = new MutableLiveData<>(UiState.IDLE);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<NearbyPlace>> nearbyPlaces = new MutableLiveData<>();
    private final MutableLiveData<PlannedRoute> plannedRoute = new MutableLiveData<>();

    private double userLng;
    private double userLat;
    private List<NearbyPlace> lastFetchedPlaces;
    private int searchToken = 0;

    @Inject
    public NearbyRouteViewModel(NearbyRouteRepository repository) {
        this.repository = repository;
    }

    public LiveData<UiState> getUiState() { return uiState; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<NearbyPlace>> getNearbyPlaces() { return nearbyPlaces; }
    public LiveData<PlannedRoute> getPlannedRoute() { return plannedRoute; }

    public void setUserLocation(double lng, double lat) {
        userLng = lng;
        userLat = lat;
    }

    public void findNearbyPlaces() {
        searchToken++;
        uiState.setValue(UiState.LOADING);
        observe(repository.getNearbyPlaces(userLng, userLat, mapboxToken()),
                nearbyPlaces, UiState.IDLE, searchToken,
                data -> lastFetchedPlaces = data);
    }

    public void searchByQuery(String query) {
        searchToken++;
        observe(repository.searchByQuery(query, userLng, userLat, mapboxToken()),
                nearbyPlaces, UiState.IDLE, searchToken,
                data -> lastFetchedPlaces = data);
    }

    public void generateRoute(NearbyPlace place) {
        searchToken++;
        uiState.setValue(UiState.LOADING);
        observe(repository.generateRoute(userLng, userLat, place.lng, place.lat, mapboxToken()),
                plannedRoute, UiState.ROUTE_READY, searchToken, null);
    }

    public void reset() {
        plannedRoute.setValue(null);
        nearbyPlaces.setValue(null);
        lastFetchedPlaces = null;
        errorMessage.setValue(null);
        uiState.setValue(UiState.IDLE);
    }

    /** Cancel a pending load and return to IDLE. Increments token so any in-flight result is discarded. */
    public void cancelLoading() {
        searchToken++;
        uiState.setValue(UiState.IDLE);
    }

    /** Call after inline results have been shown to prevent re-showing on rotation. */
    public void clearNearbyPlaces() { nearbyPlaces.setValue(null); }

    /** Call after the error toast has been shown to prevent re-showing on rotation. */
    public void clearError() { errorMessage.setValue(null); }

    /** Returns the last fetched places list, or null if none. Used to re-show dialog without re-fetching. */
    public List<NearbyPlace> getLastFetchedPlaces() { return lastFetchedPlaces; }

    private String mapboxToken() {
        return MapboxOptions.INSTANCE.getAccessToken();
    }

    interface AfterSuccess<T> { void accept(T data); }

    private <T> void observe(LiveData<Result<T>> source, MutableLiveData<T> target,
                              UiState onSuccess, int token, AfterSuccess<T> after) {
        source.observeForever(new Observer<Result<T>>() {
            @Override
            public void onChanged(Result<T> r) {
                if (r instanceof Result.Loading) return;
                source.removeObserver(this);
                // Discard result if this request was cancelled or superseded by a newer one
                if (token != searchToken) return;
                if (r instanceof Result.Success) {
                    T data = ((Result.Success<T>) r).data;
                    target.setValue(data);
                    uiState.setValue(onSuccess);
                    if (after != null) after.accept(data);
                } else if (r instanceof Result.Error) {
                    errorMessage.setValue(((Result.Error<T>) r).message);
                    uiState.setValue(UiState.IDLE);
                }
            }
        });
    }
}
