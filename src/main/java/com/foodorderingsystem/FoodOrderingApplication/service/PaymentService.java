package com.foodorderingsystem.FoodOrderingApplication.service;


import com.foodorderingsystem.FoodOrderingApplication.entity.Order;
import com.foodorderingsystem.FoodOrderingApplication.entity.Payment;

import java.util.Optional;

public interface PaymentService {
    Payment processPayment(Order order);
    Optional<Payment> getPaymentStatus(Long orderId);
}
