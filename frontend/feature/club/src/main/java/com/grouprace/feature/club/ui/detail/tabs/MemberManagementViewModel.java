package com.grouprace.feature.club.ui.detail.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.ClubRepository;
import com.grouprace.core.model.ClubMember;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MemberManagementViewModel extends ViewModel {
    private final ClubRepository clubRepository;
    
    private final MutableLiveData<Integer> clubId = new MutableLiveData<>();
    private final MutableLiveData<Result<List<ClubMember>>> members = new MutableLiveData<>();
    private final MutableLiveData<Result<String>> actionResult = new MutableLiveData<>();

    @Inject
    public MemberManagementViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public void setClubId(int id) {
        if (clubId.getValue() != null && clubId.getValue() == id) return;
        clubId.setValue(id);
        loadMembers();
    }

    public void loadMembers() {
        if (clubId.getValue() == null) return;
        members.postValue(new Result.Loading<>());
        clubRepository.getMembers(clubId.getValue()).observeForever(members::postValue);
    }

    public LiveData<Result<List<ClubMember>>> getMembers() {
        return members;
    }

    public LiveData<Result<String>> getActionResult() {
        return actionResult;
    }

    public void updateMemberStatus(int userId, String status) {
        if (clubId.getValue() == null) return;
        actionResult.postValue(new Result.Loading<>());
        clubRepository.updateMemberStatus(clubId.getValue(), userId, status).observeForever(result -> {
            actionResult.postValue(result);
            if (result instanceof Result.Success) {
                loadMembers();
            }
        });
    }

    public void updateMemberRole(int userId, String role) {
        if (clubId.getValue() == null) return;
        actionResult.postValue(new Result.Loading<>());
        clubRepository.updateMemberRole(clubId.getValue(), userId, role).observeForever(result -> {
            actionResult.postValue(result);
            if (result instanceof Result.Success) {
                loadMembers();
            }
        });
    }
    public void transferLeadership(int userId) {
        if (clubId.getValue() == null) return;
        actionResult.postValue(new Result.Loading<>());
        clubRepository.transferLeadership(clubId.getValue(), userId).observeForever(result -> {
            actionResult.postValue(result);
            if (result instanceof Result.Success) {
                loadMembers();
            }
        });
    }
}
