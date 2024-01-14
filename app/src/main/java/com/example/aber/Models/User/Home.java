package com.example.aber.Models.User;

public class Home {
    private String address;
    private String image;
    private double latitude;
    private double longitude;

    public Home(){};

    public Home(String address, String image, double latitude, double longitude) {
        this.address = address;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}
