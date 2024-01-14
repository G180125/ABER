package com.example.aber.Models.Booking;

public class BookingResponse {
    private String id;
    private Booking booking;
    private String userID;
    private String driverID;

    public BookingResponse(){}

    public BookingResponse(Booking booking, String userID, String driverID) {
        this.booking = booking;
        this.userID = userID;
        this.driverID = driverID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }
}
