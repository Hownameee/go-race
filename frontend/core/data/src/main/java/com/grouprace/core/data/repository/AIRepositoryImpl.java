package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.AIChatContext;
import com.grouprace.core.model.AIRouteSuggestion;
import com.grouprace.core.model.ChatMessage;
import com.grouprace.core.network.model.ai.AIChatRequest;
import com.grouprace.core.network.model.ai.AIChatResponse;
import com.grouprace.core.network.source.AINetworkDataSource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AIRepositoryImpl implements AIRepository {
    private final AINetworkDataSource aiNetworkDataSource;

    @Inject
    public AIRepositoryImpl(AINetworkDataSource aiNetworkDataSource) {
        this.aiNetworkDataSource = aiNetworkDataSource;
    }

    @Override
    public LiveData<Result<AIRouteSuggestion>> chat(AIChatContext context) {
        // Map Domain Context to Network Request
        List<AIChatRequest.ChatMessage> networkHistory = new ArrayList<>();
        for (ChatMessage msg : context.getHistory()) {
            networkHistory.add(new AIChatRequest.ChatMessage(
                msg.isUser() ? "user" : "assistant",
                msg.getText()
            ));
        }

        List<AIChatRequest.Waypoint> networkWaypoints = new ArrayList<>();
        if (context.getCurrentWaypoints() != null) {
            for (double[] wp : context.getCurrentWaypoints()) {
                networkWaypoints.add(new AIChatRequest.Waypoint(wp[1], wp[0]));
            }
        }

        AIChatRequest request = new AIChatRequest(
            context.getPrompt(),
            networkHistory,
            new AIChatRequest.Location(context.getLatitude(), context.getLongitude()),
            networkWaypoints
        );

        // Call Network and Map Result back to Domain Model
        return Transformations.map(aiNetworkDataSource.chat(request), aiResult -> {
            if (aiResult instanceof Result.Success) {
                AIChatResponse response = ((Result.Success<AIChatResponse>) aiResult).data;
                if (response != null) {
                    List<double[]> coords = new ArrayList<>();
                    if (response.getWaypoints() != null) {
                        for (AIChatResponse.Waypoint wp : response.getWaypoints()) {
                            coords.add(new double[]{wp.getLongitude(), wp.getLatitude()});
                        }
                    }
                    return new Result.Success<>(new AIRouteSuggestion(response.getExplanation(), coords));
                }
            } else if (aiResult instanceof Result.Error) {
                return new Result.Error<>(((Result.Error<?>) aiResult).exception, ((Result.Error<?>) aiResult).message);
            }
            return new Result.Loading<>();
        });
    }
}
