package com.grouprace.feature.profile.ui.main.stats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.repository.RecordRepository;
import com.grouprace.core.model.Profile.WeeklyRecordSummary;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileStatsViewModel extends ViewModel {
  private static final int WEEKLY_SUMMARY_WINDOW = 12;
  /** Only "Running" activities are surfaced in stats (walking is not tracked). */
  private static final String ACTIVITY_TYPE = "Running";

  private final RecordRepository recordRepository;
  private final MutableLiveData<Result<WeeklyRecordSummary>> weeklySummary = new MutableLiveData<>();
  private LiveData<Result<WeeklyRecordSummary>> weeklySummarySource;
  private Observer<Result<WeeklyRecordSummary>> weeklySummaryObserver;
  private int userId = -1;
  private boolean self = true;
  private boolean initialized;

  @Inject
  public ProfileStatsViewModel(RecordRepository recordRepository) {
    this.recordRepository = recordRepository;
  }

  public void initialize(int userId, boolean self) {
    if (initialized && this.userId == userId && this.self == self) {
      return;
    }
    this.userId = userId;
    this.self = self;
    this.initialized = true;
    loadWeeklySummary();
  }

  public LiveData<Result<WeeklyRecordSummary>> getWeeklySummary() {
    return weeklySummary;
  }

  private void loadWeeklySummary() {
    if (!initialized || (!self && userId <= 0)) {
      return;
    }
    if (weeklySummarySource != null && weeklySummaryObserver != null) {
      weeklySummarySource.removeObserver(weeklySummaryObserver);
    }

    weeklySummarySource = self
        ? recordRepository.getMyWeeklySummary(ACTIVITY_TYPE, WEEKLY_SUMMARY_WINDOW)
        : recordRepository.getUserWeeklySummary(userId, ACTIVITY_TYPE, WEEKLY_SUMMARY_WINDOW);
    weeklySummaryObserver = result -> weeklySummary.setValue(result);
    weeklySummarySource.observeForever(weeklySummaryObserver);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (weeklySummarySource != null && weeklySummaryObserver != null) {
      weeklySummarySource.removeObserver(weeklySummaryObserver);
    }
  }
}
