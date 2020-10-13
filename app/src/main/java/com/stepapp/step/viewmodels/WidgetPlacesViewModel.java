package com.stepapp.step.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.stepapp.step.BR;
import com.stepapp.step.utils.MyPlaces;

public class WidgetPlacesViewModel extends BaseObservable {
    private String id, address, title;

    public WidgetPlacesViewModel(String id, String address) {
        this.id = id;
        this.address = address;
    }



    public WidgetPlacesViewModel(String id, String address, String title) {
        this.id = id;
        this.address = address;
        this.title = title;
    }

    public WidgetPlacesViewModel(MyPlaces myPlaces) {
        this.id = myPlaces.getId();
        this.address = myPlaces.getAddress();
        this.title = myPlaces.getTitle();
    }

    public WidgetPlacesViewModel() {
    }

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (this.id != id) {
            this.id = id;
            notifyPropertyChanged(BR.id);
        }
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (this.address != address) {
            this.address = address;
            notifyPropertyChanged(BR.address);
        }
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (this.title != title) {
            this.title = title;
            notifyPropertyChanged(BR.title);
        }
    }
}
