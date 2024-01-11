package com.example.aber.Models.Booking;

public class PickUp {
    private String address;
    private double latitude;
    private double longitude;
    private String realPickUpTime;
    private String pickUpImage;

    public PickUp() {}

    public PickUp(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getRealPickUpTime() {
        return realPickUpTime;
    }

    public void setRealPickUpTime(String realPickUpTime) {
        this.realPickUpTime = realPickUpTime;
    }

    public String getPickUpImage() {
        return pickUpImage;
    }

    public void setPickUpImage(String pickUpImage) {
        this.pickUpImage = pickUpImage;
    }
}
