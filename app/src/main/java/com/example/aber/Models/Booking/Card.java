package com.example.aber.Models.Booking;

public class Card {
    private String cardNumber;
    private String brand;
    private int expMonth;
    private int expYear;
    private String cardHolder;
    private int last4;


    public Card(){}

    public Card(String cardNumber, String brand, int expMonth, int expYear, String cardHolder, int last4) {
        this.cardNumber = cardNumber;
        this.brand = brand;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.cardHolder = cardHolder;
        this.last4 = last4;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public int getLast4() {
        return last4;
    }

    public void setLast4(int last4) {
        this.last4 = last4;
    }
}
