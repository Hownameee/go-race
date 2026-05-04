package com.grouprace.core.sync;

import android.content.Context;
import android.net.ConnectivityManager;
import android. net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.data.NetworkMonitor;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class NetworkMonitorImpl implements NetworkMonitor {

    private final MutableLiveData<Boolean> _isOnline = new MutableLiveData<>(false);
    private final ConnectivityManager connectivityManager;

    @Inject
    public NetworkMonitorImpl(@ApplicationContext Context context) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        // Initialize current state
        _isOnline.postValue(isCurrentlyOnline());

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                _isOnline.postValue(true);
            }

            @Override
            public void onLost(@NonNull Network network) {
                _isOnline.postValue(isCurrentlyOnline());
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                _isOnline.postValue(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET));
            }
        });
    }

    private boolean isCurrentlyOnline() {
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    @Override
    public LiveData<Boolean> getIsOnline() {
        return _isOnline;
    }
}
