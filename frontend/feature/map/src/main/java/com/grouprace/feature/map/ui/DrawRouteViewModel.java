package com.grouprace.feature.map.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.UserRouteRepository;
import com.grouprace.core.model.PlannedRoute;
import com.grouprace.core.model.UserRoute;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DrawRouteViewModel extends ViewModel {

    public enum ScreenMode { BROWSE, PREVIEW, DRAWING }
    public enum DrawingState { IDLE, LOADING, ROUTE_READY }

    private final UserRouteRepository repository;

    private final MutableLiveData<ScreenMode> _screenMode = new MutableLiveData<>(ScreenMode.BROWSE);
    public LiveData<ScreenMode> screenMode = _screenMode;

    private final MutableLiveData<List<double[]>> _waypoints = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<double[]>> waypoints = _waypoints;

    private final MutableLiveData<Boolean> _isCycle = new MutableLiveData<>(false);
    public LiveData<Boolean> isCycle = _isCycle;

    private final MutableLiveData<PlannedRoute> _generatedRoute = new MutableLiveData<>(null);
    public LiveData<PlannedRoute> generatedRoute = _generatedRoute;

    private final MutableLiveData<DrawingState> _drawingState = new MutableLiveData<>(DrawingState.IDLE);
    public LiveData<DrawingState> drawingState = _drawingState;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>(null);
    public LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<UserRoute> _previewedRoute = new MutableLiveData<>(null);
    public LiveData<UserRoute> previewedRoute = _previewedRoute;

    private final MutableLiveData<String> _accessToken = new MutableLiveData<>(null);
    public final LiveData<List<UserRoute>> savedRoutes;

    private static class RouteRequest {
        final List<double[]> waypoints;
        final boolean isCycle;
        final String token;

        RouteRequest(List<double[]> waypoints, boolean isCycle, String token) {
            this.waypoints = waypoints;
            this.isCycle = isCycle;
            this.token = token;
        }
    }

    private final MediatorLiveData<RouteRequest> _routeTrigger = new MediatorLiveData<>();

    public final LiveData<Result<PlannedRoute>> routeResult;

    @Inject
    public DrawRouteViewModel(UserRouteRepository repository) {
        this.repository = repository;
        this.savedRoutes = repository.getAllRoutes();

        _routeTrigger.addSource(_waypoints, w -> updateTrigger());
        _routeTrigger.addSource(_isCycle, c -> updateTrigger());
        _routeTrigger.addSource(_accessToken, t -> updateTrigger());

        routeResult = Transformations.switchMap(_routeTrigger, request -> {
            if (request.waypoints == null || request.waypoints.size() < 2 || request.token == null) {
                _drawingState.setValue(DrawingState.IDLE);
                return new MutableLiveData<>(null);
            }
            _drawingState.setValue(DrawingState.LOADING);
            return repository.generateRouteFromWaypoints(request.waypoints, "normal", request.isCycle, request.token);
        });
    }

    private void updateTrigger() {
        List<double[]> w = _waypoints.getValue();
        Boolean c = _isCycle.getValue();
        String t = _accessToken.getValue();
        if (w != null && c != null && t != null) {
            _routeTrigger.setValue(new RouteRequest(w, c, t));
        }
    }

    public void setAccessToken(String token) {
        _accessToken.setValue(token);
    }

    public void enterDrawingMode() {
        clearAll();
        _screenMode.setValue(ScreenMode.DRAWING);
    }

    public void exitDrawingMode() {
        _screenMode.setValue(ScreenMode.BROWSE);
        _previewedRoute.setValue(null);
        clearAll();
    }

    public void addWaypoint(double lng, double lat) {
        List<double[]> current = new ArrayList<>(_waypoints.getValue());
        if (current.size() >= 25) {
            _errorMessage.setValue("Maximum 25 waypoints reached");
            return;
        }
        current.add(new double[]{lng, lat});
        _waypoints.setValue(current);
        _generatedRoute.setValue(null);
        _drawingState.setValue(DrawingState.IDLE);
    }

    public void undoLastWaypoint() {
        List<double[]> current = new ArrayList<>(_waypoints.getValue());
        if (!current.isEmpty()) {
            current.remove(current.size() - 1);
            _waypoints.setValue(current);
            _generatedRoute.setValue(null);
            _drawingState.setValue(DrawingState.IDLE);
        }
    }

    public void clearAll() {
        _waypoints.setValue(new ArrayList<>());
        _generatedRoute.setValue(null);
        _drawingState.setValue(DrawingState.IDLE);
        _errorMessage.setValue(null);
    }

    public void setCycle(boolean cycle) {
        if (cycle != _isCycle.getValue()) {
            _isCycle.setValue(cycle);
            _generatedRoute.setValue(null);
            _drawingState.setValue(DrawingState.IDLE);
        }
    }

    public void handleRouteResult(Result<PlannedRoute> result) {
        if (result == null) return;
        if (result instanceof Result.Success) {
            _generatedRoute.setValue(((Result.Success<PlannedRoute>) result).data);
            _drawingState.setValue(DrawingState.ROUTE_READY);
        } else if (result instanceof Result.Error) {
            _drawingState.setValue(DrawingState.IDLE);
            _generatedRoute.setValue(null);
            _errorMessage.setValue("Failed to generate route");
        }
    }

    public void handleAIRoutingResult(AIRoutingResult result) {
        if (result == null) return;
        _generatedRoute.setValue(result.getRoute());
        _drawingState.setValue(DrawingState.ROUTE_READY);
        
        if (result.getWaypoints() != null && !result.getWaypoints().isEmpty()) {
            _waypoints.setValue(new ArrayList<>(result.getWaypoints()));
        }
    }

    public void saveRoute(String name) {
        PlannedRoute routeData = _generatedRoute.getValue();
        if (routeData == null) {
            _errorMessage.setValue("Generate route before saving");
            return;
        }

        UserRoute route = new UserRoute(0, name, _waypoints.getValue(), routeData.getCoordinates(),
                routeData.getDistanceKm(), routeData.getDurationSeconds(), "normal",
                _isCycle.getValue(), System.currentTimeMillis());

        repository.saveRoute(route).observeForever(result -> {
            if (result instanceof Result.Success) {
                exitDrawingMode();
            } else {
                _errorMessage.setValue("Failed to save route");
            }
        });
    }

    /** Show route preview with "Use" and "Edit" options */
    public void previewRoute(UserRoute route) {
        _previewedRoute.setValue(route);
        _screenMode.setValue(ScreenMode.PREVIEW);
    }

    /** Transition from PREVIEW to full DRAWING/editing mode */
    public void editPreviewedRoute() {
        UserRoute route = _previewedRoute.getValue();
        if (route == null) return;
        loadRoute(route);
    }

    public void exitPreview() {
        _previewedRoute.setValue(null);
        _screenMode.setValue(ScreenMode.BROWSE);
        clearAll();
    }

    private void loadRoute(UserRoute route) {
        clearAll();
        _waypoints.setValue(route.waypoints);
        _isCycle.setValue(route.isCycle);
        
        PlannedRoute planned = new PlannedRoute(route.routeCoordinates, route.distanceKm, route.durationSeconds);
        _generatedRoute.setValue(planned);
        
        _screenMode.setValue(ScreenMode.DRAWING);
        _drawingState.setValue(DrawingState.ROUTE_READY);
    }

    public void deleteRoute(UserRoute route) {
        repository.deleteRoute(route.id);
    }

    public void clearErrorMessage() {
        _errorMessage.setValue(null);
    }
}
