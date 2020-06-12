package com.truckdriverco.truckdriver.Model;

public class Order {
    int id;
    double totalTons;
    String badgeName;
    double totalPayment;
    String OrderComment;
    int shipmentPrice;
    String destination;
    String customerName;
    String order_Date;
    int orderStatues;
    int administratorID;
    int bakeryID;
    String truckDriverID;

    public Order() {
    }

    public Order(int id, double totalTons, String badgeName, double totalPayment, String orderComment, int shipmentPrice, String destination, String customerName, String order_Date, int orderStatues, int AdministratorID, int bakeryID, String truckDriverID) {
        this.id = id;
        this.totalTons = totalTons;
        this.badgeName = badgeName;
        this.totalPayment = totalPayment;
        OrderComment = orderComment;
        this.shipmentPrice = shipmentPrice;
        this.destination = destination;
        this.customerName = customerName;
        this.order_Date = order_Date;
        this.orderStatues = orderStatues;
        this.administratorID = AdministratorID;
        this.bakeryID = bakeryID;
        this.truckDriverID = truckDriverID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotalTons() {
        return totalTons;
    }

    public void setTotalTons(double totalTons) {
        this.totalTons = totalTons;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public String getOrderComment() {
        return OrderComment;
    }

    public void setOrderComment(String orderComment) {
        OrderComment = orderComment;
    }

    public int getShipmentPrice() {
        return shipmentPrice;
    }

    public void setShipmentPrice(int shipmentPrice) {
        this.shipmentPrice = shipmentPrice;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrder_Date() {
        return order_Date;
    }

    public void setOrder_Date(String order_Date) {
        this.order_Date = order_Date;
    }

    public int getOrderStatues() {
        return orderStatues;
    }

    public void setOrderStatues(int orderStatues) {
        this.orderStatues = orderStatues;
    }

    public int getAdministratorID() {
        return this.administratorID;
    }

    public void setAdministratorID(int administratorID) {
        this.administratorID = administratorID;
    }

    public int getBakeryID() {
        return bakeryID;
    }

    public void setBakeryID(int bakeryID) {
        this.bakeryID = bakeryID;
    }

    public String getTruckDriverID() {
        return truckDriverID;
    }

    public void setTruckDriverID(String truckDriverID) {
        this.truckDriverID = truckDriverID;
    }
}
