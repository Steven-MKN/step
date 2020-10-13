package com.stepapp.step.utils;

public class MyPlaces {
    private String id, title, address;

    public MyPlaces(String id, String title, String address) {
        this.id = id;
        this.title = title;
        this.address = address;
    }

    public MyPlaces() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
