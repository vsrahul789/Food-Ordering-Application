package com.foodorderingsystem.FoodOrderingApplication.service;


import com.foodorderingsystem.FoodOrderingApplication.entity.Order;
import com.foodorderingsystem.FoodOrderingApplication.entity.Payment;

public interface PaymentService {
    Payment processPayment(Order order);
    Payment getPaymentStatus(Long orderId);
}
