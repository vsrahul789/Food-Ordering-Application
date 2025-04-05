package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.entity.Order;
import com.foodorderingsystem.FoodOrderingApplication.entity.Payment;
import com.foodorderingsystem.FoodOrderingApplication.entity.enums.PaymentStatus;
import com.foodorderingsystem.FoodOrderingApplication.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setTotalAmount(50.0);
    }

    @Test
    void processPayment_shouldReturnSavedPayment() {
        Payment mockPayment = new Payment();
        mockPayment.setId(1L);
        mockPayment.setAmount(order.getTotalAmount());
        mockPayment.setOrder(order);
        mockPayment.setTransactionId("txn-123");
        mockPayment.setPaymentStatus(PaymentStatus.SUCCESS);
        mockPayment.setTimestamp(LocalDateTime.now());

        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        Payment result = paymentService.processPayment(order);

        assertNotNull(result);
        assertEquals(mockPayment.getAmount(), result.getAmount());
        assertEquals(PaymentStatus.SUCCESS, result.getPaymentStatus());
        assertEquals(order, result.getOrder());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void getPaymentStatus_shouldReturnPaymentIfExists() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrder(order);

        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentService.getPaymentStatus(1L);

        assertTrue(result.isPresent());
        assertEquals(payment, result.get());
        verify(paymentRepository).findByOrderId(1L);
    }

    @Test
    void getPaymentStatus_shouldReturnEmptyIfNotFound() {
        when(paymentRepository.findByOrderId(2L)).thenReturn(Optional.empty());

        Optional<Payment> result = paymentService.getPaymentStatus(2L);

        assertTrue(result.isEmpty());
        verify(paymentRepository).findByOrderId(2L);
    }
}
