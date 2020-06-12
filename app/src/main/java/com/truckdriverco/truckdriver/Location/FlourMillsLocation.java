package com.truckdriverco.truckdriver.Location;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class FlourMillsLocation {


    UserFlourMill user;
    GeoPoint location;
    @ServerTimestamp
    Date timestamp;


    public FlourMillsLocation(UserFlourMill user, GeoPoint location, Date timestamp) {
        this.user = user;
        this.location = location;
        this.timestamp = timestamp;
    }

    public FlourMillsLocation() {
    }


    public UserFlourMill getUser() {
        return user;
    }

    public void setUser(UserFlourMill user) {
        this.user = user;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}



