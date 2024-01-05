package com.example.aber.Models.Booking;

import com.example.aber.Models.User.Home;
import com.example.aber.Models.User.SOS;
import com.example.aber.Models.User.Vehicle;

public class Booking {
    private String id;
    private String pickUp;
    private Home destination;
    private String ETA;
    private String bookingTime;
    private String realPickUpTime;
    private String pickUpImage;
    private Payment payment;
    private Vehicle vehicle;
    private SOS emergencyContact;
    private String status;

    public Booking(){}

    public Booking(String pickUp, Home destination, String ETA, String bookingTime, String realPickUpTime, String pickUpImage, Payment payment, SOS emergencyContact, Vehicle vehicle) {
        this.id = generateID();
        this.pickUp = pickUp;
        this.destination = destination;
        this.ETA = ETA;
        this.bookingTime = bookingTime;
        this.realPickUpTime = realPickUpTime;
        this.pickUpImage = pickUpImage;
        this.payment = payment;
        this.vehicle = vehicle;
        this.emergencyContact = emergencyContact;
        this.status = "Pending";
    }

    private String generateID() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        int length = 12;

        StringBuilder idBuilder = new StringBuilder();

        while (idBuilder.length() < length) {
            int index = (int) (Math.random() * characters.length());
            char randomChar = characters.charAt(index);

            idBuilder.append(randomChar);
        }

        return idBuilder.toString();
    }

    public String getId(){return this.id;}
    public String getPickUp() {
        return pickUp;
    }

    public void setPickUp(String pickUp) {
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

    @Override
    public String toString() {
        return "Booking{" +
                "pickUp='" + pickUp + '\'' +
                ", destination=" + destination +
                ", ETA='" + ETA + '\'' +
                ", bookingTime='" + bookingTime + '\'' +
                ", realPickUpTime='" + realPickUpTime + '\'' +
                ", pickUpImage='" + pickUpImage + '\'' +
                ", payment=" + payment +
                ", vehicle=" + vehicle +
                ", emergencyContact=" + emergencyContact +
                '}';
    }
}
