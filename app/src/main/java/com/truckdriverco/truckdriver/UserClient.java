package com.truckdriverco.truckdriver;

import android.app.Application;

import com.truckdriverco.truckdriver.Model.User;


public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
