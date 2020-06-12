package com.truckdriverco.truckdriver.Location;

class OrderProducts {
    private int id;
    private String badge;
    private int price;
    private String pic;
    private int OrderId;
    private int tons;

    public OrderProducts(int id, String badgeName, int price, String url, int orderId, int tons) {
        this.id = id;
        badge = badgeName;
        this.price = price;
        this.pic = url;
        this.OrderId = orderId;
        this.tons = tons;
    }

    public OrderProducts() {
    }

    public int getTons() {
        return tons;
    }

    public void setTons(int tons) {
        this.tons = tons;
    }

    public String getUrl() {
        return pic;
    }

    public void setUrl(String url) {
        this.pic = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBadgeName() {
        return badge;
    }

    public void setBadgeName(String badgeName) {
        badge = badgeName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }
}
