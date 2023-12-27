package com.example.aber.Models.Booking;

import com.example.aber.Models.User.SOS;

public class Booking {
    private String pickUp;
    private String destination;
    private String ETA;
    private String bookingTime;
    private String realPickUpTime;
    private String pickUpImage;
    private Payment payment;
    private SOS emergencyContact;

    public Booking(){}

    public Booking(String pickUp, String destination, String ETA, String bookingTime, String realPickUpTime, String pickUpImage, Payment payment, SOS emergencyContact) {
        this.pickUp = pickUp;
        this.destination = destination;
        this.ETA = ETA;
        this.bookingTime = bookingTime;
        this.realPickUpTime = realPickUpTime;
        this.pickUpImage = pickUpImage;
        this.payment = payment;
        this.emergencyContact = emergencyContact;
    }

    public String getPickUp() {
        return pickUp;
    }

    public void setPickUp(String pickUp) {
        this.pickUp = pickUp;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
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

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
