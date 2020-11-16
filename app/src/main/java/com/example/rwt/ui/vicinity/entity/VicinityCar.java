package com.example.rwt.ui.vicinity.entity;

public class VicinityCar {
    private int id;
    private String carcolor_url;
    private String title;
    private String distance;
    private String time;
    private String location;
    private String detailed_location;
    private double longitude;
    private double latitude;
    private String charging_status;
    private int volume;
    private int allowance;

    public VicinityCar(int id, String carcolor_url, String title, String distance, String time, String location, String detailed_location, double longitude, double latitude, String charging_status, int volume, int allowance) {
        this.id = id;
        this.carcolor_url = carcolor_url;
        this.title = title;
        this.distance = distance;
        this.time = time;
        this.location = location;
        this.detailed_location = detailed_location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.charging_status = charging_status;
        this.volume = volume;
        this.allowance = allowance;
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

    public String getDetailed_location() {
        return detailed_location;
    }

    public void setDetailed_location(String detailed_location) {
        this.detailed_location = detailed_location;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCharging_status() {
        return charging_status;
    }

    public void setCharging_status(String charging_status) {
        this.charging_status = charging_status;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getAllowance() {
        return allowance;
    }

    public void setAllowance(int allowance) {
        this.allowance = allowance;
    }
}
