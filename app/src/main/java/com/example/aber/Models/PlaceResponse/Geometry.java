package com.example.aber.Models.PlaceResponse;

public class Geometry {
    Location LocationObject;
    Viewport ViewportObject;


    // Getter Methods

    public Location getLocation() {
        return LocationObject;
    }

    public Viewport getViewport() {
        return ViewportObject;
    }

    // Setter Methods

    public void setLocation(Location locationObject) {
        this.LocationObject = locationObject;
    }

    public void setViewport(Viewport viewportObject) {
        this.ViewportObject = viewportObject;
    }
}
