package com.example.aber.Models.Booking;

public class Payment {
    private String id;
    private Double amount;
    private String currency;
    private PaymentStatus status;
    private Card card;

    private Payment(){}

    public Payment(String id, Double amount, String currency, PaymentStatus status, Card card) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.card = card;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
