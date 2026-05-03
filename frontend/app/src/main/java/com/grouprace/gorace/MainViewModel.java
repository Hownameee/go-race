package com.grouprace.gorace;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.data.repository.AuthRepository;
import com.grouprace.core.data.repository.NotificationRepository;
import com.grouprace.core.data.repository.PostRepository;
import com.grouprace.core.data.repository.RecordRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final PostRepository postRepository;
    private final RecordRepository recordRepository;
    private final NotificationRepository notificationRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    public MainViewModel(AuthRepository authRepository,
                        PostRepository postRepository,
                        RecordRepository recordRepository,
                        NotificationRepository notificationRepository) {
        this.authRepository = authRepository;
        this.postRepository = postRepository;
        this.recordRepository = recordRepository;
        this.notificationRepository = notificationRepository;
    }

    public void performGarbageCollection() {
        executorService.execute(() -> {
            postRepository.deleteOldPosts();
            recordRepository.deleteOldRecords();
        });
    }

    public void markAsRead(int notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return authRepository.getIsLoggedIn();
    }

    public LiveData<Integer> getUnreadNotificationCount() {
        return notificationRepository.getUnreadCount();
    }

    public void logout() {
        authRepository.logout();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}