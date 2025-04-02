package com.foodorderingsystem.FoodOrderingApplication.controller;

import com.foodorderingsystem.FoodOrderingApplication.dto.StripeRequest;
import com.foodorderingsystem.FoodOrderingApplication.dto.StripeResponse;
import com.foodorderingsystem.FoodOrderingApplication.entity.Order;
import com.foodorderingsystem.FoodOrderingApplication.entity.Payment;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.repository.OrderRepository;
import com.foodorderingsystem.FoodOrderingApplication.service.PaymentService;
import com.foodorderingsystem.FoodOrderingApplication.service.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestParam Long orderId) {
        User user = getAuthenticatedUser();
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!existingOrder.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only process your own orders!");
        }

        Payment payment = paymentService.processPayment(existingOrder);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody StripeRequest stripeRequest) {
        try {
            StripeResponse response = stripeService.checkoutProducts(stripeRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment processing failed: " + e.getMessage());
        }
    }
}