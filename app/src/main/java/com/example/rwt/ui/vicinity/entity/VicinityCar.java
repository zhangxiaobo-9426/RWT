package com.example.rwt.ui.vicinity.entity;

public class VicinityCar {
    private int id;
    private String carcolor_url;
    private String title;
    private String distance;
    private String time;
    private String location;

    public VicinityCar(int id, String carcolor_url, String title, String distance, String time, String location) {
        this.id = id;
        this.carcolor_url = carcolor_url;
        this.title = title;
        this.distance = distance;
        this.time = time;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarcolor_url() {
        return carcolor_url;
    }

    public void setCarcolor_url(String carcolor_url) {
        this.carcolor_url = carcolor_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
