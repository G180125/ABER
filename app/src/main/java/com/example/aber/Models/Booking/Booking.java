package com.example.aber.Models.Booking;

import com.example.aber.Models.Staff.Driver;
import com.example.aber.Models.User.Home;
import com.example.aber.Models.User.SOS;
import com.example.aber.Models.User.User;
import com.example.aber.Models.User.Vehicle;

public class Booking {
    private String id;
    private PickUp pickUp;
    private Home destination;
    private String ETA;
    private String bookingTime;
    private String bookingDate;
    private Payment payment;
    private Vehicle vehicle;
    private SOS emergencyContact;
    private String status;
    private String userID;
    private String driverID;

    public Booking(){}

    public Booking(PickUp pickUp, Home destination, String ETA, String bookingTime, Payment payment, SOS emergencyContact, Vehicle vehicle, String user, String bookingDate) {
        this.id = generateID();
        this.pickUp = pickUp;
        this.destination = destination;
        this.ETA = ETA;
        this.bookingTime = bookingTime;
        this.payment = payment;
        this.vehicle = vehicle;
        this.emergencyContact = emergencyContact;
        this.status = "Pending";
        this.userID = user;
        this.driverID = "";
        this.bookingDate = bookingDate;
    }

    private String generateID() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        int length = 16;

        StringBuilder idBuilder = new StringBuilder();

        while (idBuilder.length() < length) {
            int index = (int) (Math.random() * characters.length());
            char randomChar = characters.charAt(index);

            idBuilder.append(randomChar);
        }

        return idBuilder.toString();
    }

    public String getId(){return this.id;}
    public PickUp getPickUp() {
        return pickUp;
    }

    public void setPickUp(PickUp pickUp) {
        this.pickUp = pickUp;
    }

    public Home getDestination() {
        return destination;
    }

    public void setDestination(Home destination) {
        this.destination = destination;
    }

    public String getETA() {
        return ETA;
    }

    public void setETA(String ETA) {
        this.ETA = ETA;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public SOS getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(SOS emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser() {
        return userID;
    }

    public void setUser(String user) {
        this.userID = user;
    }

    public String getDriver() {
        return driverID;
    }

    public void setDriver(String driver) {
        this.driverID = driver;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
}
