package com.nnkwachi.firebase_test;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> selected = new MutableLiveData<Boolean>();

    public void select(Boolean aBoolean) {
        selected.setValue(aBoolean);
    }

    public LiveData<Boolean> getSelected() {
        return selected;
    }
}
