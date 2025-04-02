package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.entity.*;
import com.foodorderingsystem.FoodOrderingApplication.entity.enums.OrderStatus;
import com.foodorderingsystem.FoodOrderingApplication.exception.BadRequestException;
import com.foodorderingsystem.FoodOrderingApplication.exception.ResourceNotFoundException;
import com.foodorderingsystem.FoodOrderingApplication.exception.UnauthorizedAccessException;
import com.foodorderingsystem.FoodOrderingApplication.exception.UserNotFoundException;
import com.foodorderingsystem.FoodOrderingApplication.repository.*;
import com.foodorderingsystem.FoodOrderingApplication.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(CartRepository cartRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        log.error("Principal: {}", principal.getClass().getName());
        if (principal instanceof User) {
            String email = ((User) principal).getEmail();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
        } else {
            throw new UnauthorizedAccessException("Invalid authentication principal");
        }
    }

    private void validateUserAccess(Long userId, User user) {
        if (!user.getId().equals(userId)) {
            throw new UnauthorizedAccessException("You can only manage your own orders!");
        }
    }

    @Override
    public Order placeOrder(Long userId) {
        User user = getAuthenticatedUser();
        validateUserAccess(userId, user);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) throw new BadRequestException("Cart is empty");


        Order order = new Order();
        order.setUser(user);

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(cartItem.getMenuItem());
            orderItem.setQuantity(cartItem.getQuantity());
            total += cartItem.getMenuItem().getPrice() * cartItem.getQuantity();
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);

        cart.getItems().clear();
        cartRepository.save(cart);
        log.info("User {} placed an order with total amount: ${}", user.getName(), total);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrders(Long userId) {
        User user = getAuthenticatedUser();
        validateUserAccess(userId, user);

        log.info("Fetching orders for user {}", user.getName());
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order updateOrderStatus(Long orderId, String status) {
        User user = getAuthenticatedUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(OrderStatus.valueOf(status));
        log.info("Admin {} updated order {} status to {}", user.getName(), orderId, status);
        return orderRepository.save(order);
    }
}
