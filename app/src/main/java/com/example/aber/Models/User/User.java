package com.example.aber.Models.User;

import androidx.annotation.NonNull;

import com.example.aber.Models.Booking.Booking;
import com.example.aber.Models.Staff.Driver;

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
    private String stripeCusId;
    private List<Driver>  chattedDriver;

    public User(){};

    public User(String email, String name, Gender gender, String phoneNumber, List<Home> homes, List<Vehicle> vehicles, List<SOS> emergencyContacts, String stripeCusId) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.avatar = "";
        this.homes = homes;
        this.vehicles = vehicles;
        this.emergencyContacts = emergencyContacts;
        this.bookings = new ArrayList<>();
        this.stripeCusId = stripeCusId;
        this.chattedDriver = new ArrayList<>();
    }

    @NonNull
    public User clone() {

        User newUser = new User();
        newUser.setName(this.getName());
        newUser.setEmail(this.getEmail());
        newUser.setPhoneNumber(this.phoneNumber);
        newUser.setGender(this.gender);
        newUser.setAvatar(this.avatar);
        newUser.setHomes(this.homes);
        newUser.setVehicles(this.vehicles);
        newUser.setEmergencyContacts(this.emergencyContacts);
        return newUser;
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

    public String getStripeCusId() {
        return stripeCusId;
    }

    public void setStripeCusId(String stripeCusId) {
        this.stripeCusId = stripeCusId;
    }

    public List<Driver> getChattedDriver() {
        return chattedDriver;
    }

    public void setChattedDriver(List<Driver> chattedDriver) {
        this.chattedDriver = chattedDriver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) &&
                Objects.equals(name, user.name) &&
                gender == user.gender &&
                Objects.equals(phoneNumber, user.phoneNumber) &&
                Objects.equals(avatar, user.avatar) &&
                Objects.equals(homes, user.homes) &&
                Objects.equals(vehicles, user.vehicles) &&
                Objects.equals(emergencyContacts, user.emergencyContacts);
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