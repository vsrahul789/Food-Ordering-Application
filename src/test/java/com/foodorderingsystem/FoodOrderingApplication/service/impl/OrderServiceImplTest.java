package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.entity.*;
import com.foodorderingsystem.FoodOrderingApplication.entity.enums.OrderStatus;
import com.foodorderingsystem.FoodOrderingApplication.exception.BadRequestException;
import com.foodorderingsystem.FoodOrderingApplication.exception.ResourceNotFoundException;
import com.foodorderingsystem.FoodOrderingApplication.exception.UnauthorizedAccessException;
import com.foodorderingsystem.FoodOrderingApplication.repository.CartRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.OrderRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");
        mockUser.setName("John Doe");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(mockUser);
    }

    @Test
    void placeOrder_shouldPlaceSuccessfully() {
        MenuItem item = new MenuItem();
        item.setId(1L);
        item.setName("Pizza");
        item.setPrice(10.0);
        item.setDescription("Cheese Burst");
        item.setRestaurant(null);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setMenuItem(item);
        cartItem.setQuantity(2);

        Cart cart = new Cart();
        cart.setUser(mockUser);
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.placeOrder(1L);

        assertNotNull(order);
        assertEquals(1, order.getItems().size());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(20.0, order.getTotalAmount());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrder_shouldThrow_whenUserIdMismatch() {
        mockUser.setId(2L);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

        assertThrows(UnauthorizedAccessException.class, () -> orderService.placeOrder(1L));
    }

    @Test
    void placeOrder_shouldThrow_whenCartEmpty() {
        Cart emptyCart = new Cart();
        emptyCart.setUser(mockUser);
        emptyCart.setItems(new ArrayList<>());

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(emptyCart));

        assertThrows(BadRequestException.class, () -> orderService.placeOrder(1L));
    }

    @Test
    void getOrders_shouldReturnUserOrders() {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setUser(mockUser);

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order1));

        List<Order> orders = orderService.getOrders(1L);

        assertEquals(1, orders.size());
        assertEquals(order1, orders.getFirst());
    }

    @Test
    void cancelOrder_shouldCancelSuccessfully() {
        Order order = new Order();
        order.setId(1L);
        order.setUser(mockUser);
        order.setStatus(OrderStatus.PENDING);

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L, "CANCELED");

        assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_shouldThrow_whenDelivered() {
        Order order = new Order();
        order.setId(1L);
        order.setUser(mockUser);
        order.setStatus(OrderStatus.DELIVERED);

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.cancelOrder(1L, "CANCELLED"));
    }

    @Test
    void updateOrderStatus_shouldUpdateSuccessfully() {
        Order order = new Order();
        order.setId(1L);
        order.setUser(mockUser);
        order.setStatus(OrderStatus.PENDING);

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order updated = orderService.updateOrderStatus(1L, "DELIVERED");

        assertEquals(OrderStatus.DELIVERED, updated.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_shouldThrow_whenOrderNotFound() {
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrderStatus(1L, "DELIVERED"));
    }
}
