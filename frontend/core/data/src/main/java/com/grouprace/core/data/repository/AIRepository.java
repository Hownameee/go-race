package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.AIChatContext;
import com.grouprace.core.model.AIRouteSuggestion;

public interface AIRepository {
    LiveData<Result<AIRouteSuggestion>> chat(AIChatContext context);
}
