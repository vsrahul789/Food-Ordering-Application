package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.entity.Order;
import com.foodorderingsystem.FoodOrderingApplication.entity.Payment;
import com.foodorderingsystem.FoodOrderingApplication.entity.enums.PaymentStatus;
import com.foodorderingsystem.FoodOrderingApplication.repository.PaymentRepository;
import com.foodorderingsystem.FoodOrderingApplication.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment processPayment(Order order) {
        log.debug("Initiating payment processing for order ID: {}", order.getId());
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setAmount(order.getTotalAmount());
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setTimestamp(LocalDateTime.now());
        log.info("Payment processed successfully for order ID: {} | Transaction ID: {} | Amount: ${}",
                order.getId(), payment.getTransactionId(), payment.getAmount());
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> getPaymentStatus(Long orderId) {
        log.debug("Fetching payment status for order ID: {}", orderId);
        Optional<Payment> payment = paymentRepository.findByOrderId(orderId);

        if (payment.isPresent()) {
            log.info("Payment status found for order ID: {} | Status: {}", orderId, payment.get().getPaymentStatus());
        } else {
            log.warn("No payment record found for order ID: {}", orderId);
        }

        return payment;
    }
}
