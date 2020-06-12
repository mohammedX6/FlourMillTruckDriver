package com.truckdriverco.truckdriver.Model;

import java.util.List;

public class FullOrder {

    Order order;
    List<OrderProducts> orderProducts;

    public FullOrder(Order order, List<OrderProducts> orderProducts) {
        this.order = order;
        this.orderProducts = orderProducts;
    }
}
