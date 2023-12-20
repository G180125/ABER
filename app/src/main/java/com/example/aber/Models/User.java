package com.example.aber.Models;

import java.util.List;


public class User {
    private String email;
    private String name;
    private Gender gender;
    private String phoneNumber;
    private String avatar;
    private Home home;
    private Vehicle vehicle;
    private List<SOS> emergencyContacts;

    public User(String email, String name, Gender gender, String phoneNumber, Home home, Vehicle vehicle, List<SOS> emergencyContacts) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.avatar = "";
        this.home = home;
        this.vehicle = vehicle;
        this.emergencyContacts = emergencyContacts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<SOS> getEmergencyContacts() {
        return emergencyContacts;
    }

    public void setEmergencyContacts(List<SOS> emergencyContacts) {
        this.emergencyContacts = emergencyContacts;
    }
}
