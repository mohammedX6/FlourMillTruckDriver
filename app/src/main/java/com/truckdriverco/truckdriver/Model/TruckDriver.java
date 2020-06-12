package com.truckdriverco.truckdriver.Model;


public class TruckDriver {
    String id;
    String username;
    String password;
    String email;
    String birthdate;
    long nationalid;
    String PhoneNumber;
    String JobNumber;

    public TruckDriver(String id, String username, String password, String email, String birthdate, long nationalid, String phoneNumber, String jobNumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthdate = birthdate;
        this.nationalid = nationalid;
        PhoneNumber = phoneNumber;
        JobNumber = jobNumber;
    }

    public TruckDriver(String id, String username, String email, String birthdate, String phoneNumber, String jobNumber, long nationalid) {
        this.id = id;
        this.username = username;

        this.email = email;
        this.birthdate = birthdate;
        this.nationalid = nationalid;
        PhoneNumber = phoneNumber;
        JobNumber = jobNumber;
    }


    public TruckDriver(String username, String password, String email, String birthdate, long nationalid, String phoneNumber, String jobNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthdate = birthdate;
        this.nationalid = nationalid;
        PhoneNumber = phoneNumber;
        JobNumber = jobNumber;
    }

    public TruckDriver() {

    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public long getNationalid() {
        return nationalid;
    }

    public void setNationalid(long nationalid) {
        this.nationalid = nationalid;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getJobNumber() {
        return JobNumber;
    }

    public void setJobNumber(String jobNumber) {
        JobNumber = jobNumber;
    }
}

