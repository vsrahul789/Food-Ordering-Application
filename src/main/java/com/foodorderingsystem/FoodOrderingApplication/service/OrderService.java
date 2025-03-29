package com.foodorderingsystem.FoodOrderingApplication.service;

import com.foodorderingsystem.FoodOrderingApplication.entity.Order;

import java.util.List;

public interface OrderService {
    Order placeOrder(Long userId);
    List<Order> getOrders(Long userId);
    Order updateOrderStatus(Long orderId, String status);
}
