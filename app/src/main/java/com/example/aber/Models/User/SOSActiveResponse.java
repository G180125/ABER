package com.example.aber.Models.User;

public class SOSActiveResponse {
    private String userID;
    private SOS emergencyContact;

    public SOSActiveResponse(){}

    public SOSActiveResponse(String userID, SOS emergencyContact) {
        this.userID = userID;
        this.emergencyContact = emergencyContact;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public SOS getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(SOS emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}
