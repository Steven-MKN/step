package com.stepapp.step.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.stepapp.step.BR;
import com.stepapp.step.utils.MyPlaces;

import java.util.ArrayList;

public class SavedViewModel extends BaseObservable {
    private ArrayList<MyPlaces> places;

    public SavedViewModel(ArrayList<MyPlaces> places) {
        this.places = places;
    }

    public SavedViewModel() {
    }

    @Bindable
    public ArrayList<MyPlaces> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<MyPlaces> places) {
        if (this.places != places) {
            this.places = places;
            notifyPropertyChanged(BR.places);
        }
    }
}
