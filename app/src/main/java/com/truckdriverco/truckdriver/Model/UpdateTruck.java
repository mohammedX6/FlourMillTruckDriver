package com.truckdriverco.truckdriver.Model;

public class UpdateTruck {


    String id;
    int orderStatues;
    int orderid;
    String oldId;

    public UpdateTruck() {
    }

    public UpdateTruck(String id, int orderStatues, int orderid, String oldId) {
        this.id = id;
        this.orderStatues = orderStatues;
        this.orderid = orderid;
        this.oldId = oldId;
    }

    public String getOldId() {
        return oldId;
    }

    public void setOldId(String oldId) {
        this.oldId = oldId;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrderStatues() {
        return orderStatues;
    }

    public void setOrderStatues(int orderStatues) {
        this.orderStatues = orderStatues;
    }
}
