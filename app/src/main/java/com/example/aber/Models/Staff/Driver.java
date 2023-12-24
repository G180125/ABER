package com.example.aber.Models.Staff;

public class Driver extends Staff{
    private String name;
    private String documentID;

    public Driver(){};

    public Driver(String email, String name, String documentID) {
        super(email);
        this.name = name;
        this.documentID = documentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}

