package com.truckdriverco.truckdriver.Model;


public class Register {


    String username;
    String password;
    String email;
    String birthdate;
    int nationalid;
    String PhoneNumber;
    String JobNumber;

    public Register(String username, String password, String email, String birthdate, int nationalid, String phoneNumber, String jobNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthdate = birthdate;
        this.nationalid = nationalid;
        PhoneNumber = phoneNumber;
        JobNumber = jobNumber;
    }


}
