package com.example.aber.Models.Booking;

public class Card {
    private String brand;
    private int expMonth;
    private int expYear;
    private String last4;
    private String country;


    public Card(){}

    public Card(String brand, int expMonth, int expYear, String last4, String country) {
        this.brand = brand;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.last4 = last4;
        this.country = country;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(int expMonth) {
        this.expMonth = expMonth;
    }

    public int getExpYear() {
        return expYear;
    }

    public void setExpYear(int expYear) {
        this.expYear = expYear;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
