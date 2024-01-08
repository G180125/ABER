package com.example.aber.Models.Staff;

public class Schedule {
    private String startTime;
    private String endTIme;
    private String Date;

    public Schedule(){}

    public Schedule(String startTime, String endTIme, String date) {
        this.startTime = startTime;
        this.endTIme = endTIme;
        Date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTIme() {
        return endTIme;
    }

    public void setEndTIme(String endTIme) {
        this.endTIme = endTIme;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
