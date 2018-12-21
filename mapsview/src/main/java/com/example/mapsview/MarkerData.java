package com.example.mapsview;

public class MarkerData {
    private double latitude;
    private double longitude;
    private String title;
    private int resID;

    public MarkerData(double latitude, double longitude, String title, int resID) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.resID = resID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }
}
