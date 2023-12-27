package com.example.aber.Models.User;

import com.example.aber.Models.Booking.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class User {
    private String email;
    private String name;
    private Gender gender;
    private String phoneNumber;
    private String avatar;
    private List<Home> homes;
    private List<Vehicle> vehicles;
    private List<SOS> emergencyContacts;
    private List<Booking> bookings;

    public User(){};

    public User(String email, String name, Gender gender, String phoneNumber, List<Home> homes, List<Vehicle> vehicles, List<SOS> emergencyContacts) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.avatar = "";
        this.homes = homes;
        this.vehicles = vehicles;
        this.emergencyContacts = emergencyContacts;
        this.bookings = new ArrayList<>();
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

    public List<Home> getHomes() {
        return homes;
    }

    public void setHomes(List<Home> homes) {
        this.homes = homes;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<SOS> getEmergencyContacts() {
        return emergencyContacts;
    }

    public void setEmergencyContacts(List<SOS> emergencyContacts) {
        this.emergencyContacts = emergencyContacts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return email.equals(user.email) && name.equals(user.name) && gender == user.gender && phoneNumber.equals(user.phoneNumber) && avatar.equals(user.avatar) && homes.equals(user.homes) && vehicles.equals(user.vehicles) && emergencyContacts.equals(user.emergencyContacts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, gender, phoneNumber, avatar, homes, vehicles, emergencyContacts);
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", avatar='" + avatar + '\'' +
                ", home=" + homes +
                ", vehicle=" + vehicles +
                ", emergencyContacts=" + emergencyContacts +
                '}';
    }
}