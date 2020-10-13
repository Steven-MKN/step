package com.stepapp.step.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Pref implements Serializable {
    private String name, units, favLandmark;

    public Pref(String name, String units, String favLandmark) {
        this.name = name;
        this.units = units;
        this.favLandmark = favLandmark;
    }

    public Pref() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getFavLandmark() {
        return favLandmark;
    }

    public void setFavLandmark(String favLandmark) {
        this.favLandmark = favLandmark;
    }

    @Override
    public String toString() {
        return "Pref{" +
                "name='" + name + '\'' +
                ", units='" + units + '\'' +
                ", favLandmark='" + favLandmark + '\'' +
                '}';
    }
}
