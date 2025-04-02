package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.entity.Order;
import com.foodorderingsystem.FoodOrderingApplication.entity.Payment;
import com.foodorderingsystem.FoodOrderingApplication.entity.enums.PaymentStatus;
import com.foodorderingsystem.FoodOrderingApplication.repository.PaymentRepository;
import com.foodorderingsystem.FoodOrderingApplication.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment processPayment(Order order) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setAmount(order.getTotalAmount());
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setTimestamp(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPaymentStatus(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}
