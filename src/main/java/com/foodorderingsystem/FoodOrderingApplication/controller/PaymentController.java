package com.foodorderingsystem.FoodOrderingApplication.controller;

import com.foodorderingsystem.FoodOrderingApplication.dto.StripeRequest;
import com.foodorderingsystem.FoodOrderingApplication.dto.StripeResponse;
import com.foodorderingsystem.FoodOrderingApplication.entity.Order;
import com.foodorderingsystem.FoodOrderingApplication.entity.Payment;
import com.foodorderingsystem.FoodOrderingApplication.repository.OrderRepository;
import com.foodorderingsystem.FoodOrderingApplication.service.PaymentService;
import com.foodorderingsystem.FoodOrderingApplication.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final StripeService stripeService;

    public PaymentController(PaymentService paymentService, OrderRepository orderRepository, StripeService stripeService) {
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
        this.stripeService = stripeService;
    }

    @PostMapping("/process")
    public Payment processPayment(@RequestParam Long orderId) {
        Order existingOrder = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        return paymentService.processPayment(existingOrder);
    }

    @PostMapping("/checkout")
    public StripeResponse checkout(@RequestBody StripeRequest stripeRequest) throws Exception {
        return stripeService.checkoutProducts(stripeRequest);
    }
}