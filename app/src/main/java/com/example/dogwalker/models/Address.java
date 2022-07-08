package com.example.dogwalker.models;

public class Address {
    private String name;
    private String longtitude;
    private String latitude;


    public Address(String name, String latitude, String longtitude) {
        this.longtitude = longtitude;
        this.name = name;
        this.latitude = latitude;
    }

    //firebase constructor
    public Address() {

    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }


    public String getName() {
        return name;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public void setName(String name) {
        this.name = name;
    }
}
