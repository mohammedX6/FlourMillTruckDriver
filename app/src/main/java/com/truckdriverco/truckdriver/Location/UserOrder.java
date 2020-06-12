package com.truckdriverco.truckdriver.Location;


import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class UserOrder {

    private User user;
    private Order order;
    private List<OrderProducts> all_orders;
    private GeoPoint geo_point;
    @ServerTimestamp
    private
    Date timestamp;

    public UserOrder(User user, Order order, List<OrderProducts> all_orders, GeoPoint geo_point, Date timestamp) {
        this.user = user;
        this.order = order;
        this.all_orders = all_orders;
        this.geo_point = geo_point;
        this.timestamp = timestamp;
    }

    public UserOrder() {
    }

    public List<OrderProducts> getAll_orders() {
        return all_orders;
    }

    public void setAll_orders(List<OrderProducts> all_orders) {
        this.all_orders = all_orders;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
