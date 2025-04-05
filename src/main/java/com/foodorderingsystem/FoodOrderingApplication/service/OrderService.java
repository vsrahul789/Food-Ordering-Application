package com.foodorderingsystem.FoodOrderingApplication.service;

import com.foodorderingsystem.FoodOrderingApplication.entity.Order;

import java.util.List;

public interface OrderService {
    Order placeOrder(Long userId);
    List<Order> getOrders(Long userId);
    void cancelOrder(Long orderId, String status);
    Order updateOrderStatus(Long orderId, String status);
}
