package com.grouprace.feature.records.compare.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grouprace.core.model.Record;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CompareRecordsViewModel extends ViewModel {

    private final MutableLiveData<Record> recordA = new MutableLiveData<>();
    private final MutableLiveData<Record> recordB = new MutableLiveData<>();

    @Inject
    public CompareRecordsViewModel() {
    }

    public LiveData<Record> getRecordA() {
        return recordA;
    }

    public LiveData<Record> getRecordB() {
        return recordB;
    }

    public void setRecordA(Record record) {
        recordA.setValue(record);
    }

    public void setRecordB(Record record) {
        recordB.setValue(record);
    }

    public boolean isComparisonReady() {
        return recordA.getValue() != null && recordB.getValue() != null;
    }

    /**
     * Returns 1 if valueA is "better", -1 if valueB is better, 0 if equal.
     * For Distance, Speed, Calories: Larger is better.
     * For Duration: Smaller is better? No, let's keep it consistent: Larger is better for "amount of work".
     * Actually, user said: "the higher will have color text to green".
     */
    public int compare(double valueA, double valueB, boolean higherIsBetter) {
        if (valueA == valueB) return 0;
        if (higherIsBetter) {
            return valueA > valueB ? 1 : -1;
        } else {
            return valueA < valueB ? 1 : -1;
        }
    }
}
