package com.example.aber.Models.PlaceResponse;

import java.util.ArrayList;

public class CurrentOpeningHours {
    private boolean open_now;
    ArrayList<Object> periods = new ArrayList<Object>();
    ArrayList<Object> weekday_text = new ArrayList<Object>();


    // Getter Methods

    public boolean getOpen_now() {
        return open_now;
    }

    // Setter Methods

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }
}
