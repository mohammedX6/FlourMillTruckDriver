package com.truckdriverco.truckdriver.Model;

public class FinishOrder {


    int orderStatues;
    int orderid;

    public FinishOrder(int orderStatues, int orderid) {

        this.orderStatues = orderStatues;
        this.orderid = orderid;
    }

    public FinishOrder() {
    }

    public int getOrderStatues() {
        return orderStatues;
    }

    public void setOrderStatues(int orderStatues) {
        this.orderStatues = orderStatues;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }
}
