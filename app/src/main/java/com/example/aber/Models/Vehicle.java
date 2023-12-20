package com.example.aber.Models;

import java.util.List;

public class Vehicle {
    private String brand;
    private String name;
    private String color;
    private String seatCapacity;
    private String numberPlate;
    private List<String> images;

    public Vehicle(){};

    public Vehicle(String brand, String name, String color, String seatCapacity, String numberPlate, List<String> images) {
        this.brand = brand;
        this.name = name;
        this.color = color;
        this.seatCapacity = seatCapacity;
        this.numberPlate = numberPlate;
        this.images = images;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSeatCapacity() {
        return seatCapacity;
    }

    public void setSeatCapacity(String seatCapacity) {
        this.seatCapacity = seatCapacity;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
