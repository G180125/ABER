package com.example.aber.Models.Staff;

public class Admin extends Staff{
    private String documentID;

    public Admin(){}

    public Admin(String email, String documentID) {
        super(email);
        this.documentID = documentID;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
