package com.grouprace.core.data;

import androidx.lifecycle.LiveData;

public interface NetworkMonitor {
    LiveData<Boolean> getIsOnline();
}
