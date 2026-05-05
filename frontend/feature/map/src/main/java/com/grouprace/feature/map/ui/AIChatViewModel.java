package com.grouprace.feature.map.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.AIRepository;
import com.grouprace.core.data.repository.UserRouteRepository;
import com.grouprace.core.model.AIChatContext;
import com.grouprace.core.model.AIRouteSuggestion;
import com.grouprace.core.model.ChatMessage;
import com.grouprace.core.model.PlannedRoute;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AIChatViewModel extends ViewModel {
    private final AIRepository aiRepository;
    private final UserRouteRepository userRouteRepository;
    private final List<ChatMessage> history = new ArrayList<>();
    private final List<ChatMessage> uiMessages = new ArrayList<>();
    
    private final MutableLiveData<ChatRequestPayload> _chatRequest = new MutableLiveData<>();
    private final MutableLiveData<List<ChatMessage>> _allMessages = new MutableLiveData<>(uiMessages);
    
    public final LiveData<Result<AIRoutingResult>> chatResult;
    public final LiveData<List<ChatMessage>> allMessages = _allMessages;

    private static class ChatRequestPayload {
        final AIChatContext context;
        final String mapboxAccessToken;
        ChatRequestPayload(AIChatContext context, String mapboxAccessToken) {
            this.context = context;
            this.mapboxAccessToken = mapboxAccessToken;
        }
    }

    @Inject
    public AIChatViewModel(AIRepository aiRepository, UserRouteRepository userRouteRepository) {
        this.aiRepository = aiRepository;
        this.userRouteRepository = userRouteRepository;
        
        if (uiMessages.isEmpty()) {
            uiMessages.add(new ChatMessage("Hello! I'm your AI route planner. Where would you like to go today?", false));
            _allMessages.setValue(new ArrayList<>(uiMessages));
        }
        
        this.chatResult = Transformations.switchMap(_chatRequest, payload -> 
            Transformations.switchMap(aiRepository.chat(payload.context), aiResult -> {
                if (aiResult instanceof Result.Success) {
                    AIRouteSuggestion suggestion = ((Result.Success<AIRouteSuggestion>) aiResult).data;
                    if (suggestion != null && !suggestion.getWaypoints().isEmpty()) {
                        
                        // Update UI and History with AI response
                        ChatMessage uiMsg = new ChatMessage(suggestion.getExplanation(), false);
                        history.add(uiMsg);
                        uiMessages.add(uiMsg);
                        _allMessages.postValue(new ArrayList<>(uiMessages));

                        return Transformations.map(
                            userRouteRepository.generateRouteFromWaypoints(
                                suggestion.getWaypoints(), 
                                "normal", 
                                false, 
                                payload.mapboxAccessToken
                            ),
                            routeResult -> {
                                if (routeResult instanceof Result.Success) {
                                    PlannedRoute route = ((Result.Success<PlannedRoute>) routeResult).data;
                                    return new Result.Success<>(new AIRoutingResult(route, suggestion.getWaypoints()));
                                } else if (routeResult instanceof Result.Error) {
                                    return new Result.Error<>(((Result.Error<?>) routeResult).exception, ((Result.Error<?>) routeResult).message);
                                }
                                return new Result.Loading<>();
                            }
                        );
                    } else {
                        MutableLiveData<Result<AIRoutingResult>> error = new MutableLiveData<>();
                        error.setValue(new Result.Error<>(new Exception("No waypoints generated"), "No waypoints generated"));
                        return error;
                    }
                } else if (aiResult instanceof Result.Error) {
                    MutableLiveData<Result<AIRoutingResult>> error = new MutableLiveData<>();
                    error.setValue(new Result.Error<>(((Result.Error<?>) aiResult).exception, ((Result.Error<?>) aiResult).message));
                    return error;
                } else {
                    MutableLiveData<Result<AIRoutingResult>> loading = new MutableLiveData<>();
                    loading.setValue(new Result.Loading<>());
                    return loading;
                }
            })
        );
    }

    public void sendMessage(String prompt, double lat, double lng, String mapboxAccessToken, List<double[]> currentWaypoints) {
        AIChatContext context = new AIChatContext(
            prompt,
            new ArrayList<>(history),
            lat,
            lng,
            currentWaypoints
        );
        
        // Record User Message
        ChatMessage userMsg = new ChatMessage(prompt, true);
        history.add(userMsg);
        uiMessages.add(userMsg);
        _allMessages.postValue(new ArrayList<>(uiMessages));

        _chatRequest.setValue(new ChatRequestPayload(context, mapboxAccessToken));
    }
}
