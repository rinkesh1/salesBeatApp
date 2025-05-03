package com.newsalesbeatApp.adapters;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {
    private MutableLiveData<LocationData> locationLiveData = new MutableLiveData<>();

    public LiveData<LocationData> getLocationLiveData() {
        return locationLiveData;
    }

    public void setLocationData(LocationData locationData) {
        locationLiveData.setValue(locationData);
    }
}

