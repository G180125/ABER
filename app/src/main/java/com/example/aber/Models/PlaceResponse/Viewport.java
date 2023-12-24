package com.example.aber.Models.PlaceResponse;

public class Viewport {
    Northeast NortheastObject;
    Southwest SouthwestObject;


    // Getter Methods

    public Northeast getNortheast() {
        return NortheastObject;
    }

    public Southwest getSouthwest() {
        return SouthwestObject;
    }

    // Setter Methods

    public void setNortheast(Northeast northeastObject) {
        this.NortheastObject = northeastObject;
    }

    public void setSouthwest(Southwest southwestObject) {
        this.SouthwestObject = southwestObject;
    }
}
