package com.truckdriverco.truckdriver.Location;


public class UserFlourMill {


    private String email;
    private int id;
    private String username;


    public UserFlourMill(String email, int id, String username) {
        this.email = email;
        this.id = id;
        this.username = username;
    }

    public UserFlourMill() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

