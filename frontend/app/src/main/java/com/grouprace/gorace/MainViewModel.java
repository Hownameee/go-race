package com.grouprace.gorace;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.data.dao.NotificationDao;
import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.repository.AuthRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final PostDao postDao;
    private final RecordDao recordDao;
    private final NotificationDao notificationDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    public MainViewModel(AuthRepository authRepository, PostDao postDao, RecordDao recordDao, NotificationDao notificationDao) {
        this.authRepository = authRepository;
        this.postDao = postDao;
        this.recordDao = recordDao;
        this.notificationDao = notificationDao;
    }

    public void performGarbageCollection() {
        executorService.execute(() -> {
            postDao.deleteOldPosts();
            recordDao.deleteOldRecords();
            notificationDao.deleteOldNotifications();
        });
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return authRepository.getIsLoggedIn();
    }

    public void logout() {
        authRepository.logout();
    }
}
