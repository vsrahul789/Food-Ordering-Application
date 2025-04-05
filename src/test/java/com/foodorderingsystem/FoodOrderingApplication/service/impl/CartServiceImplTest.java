package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.dto.AddToCartDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.Cart;
import com.foodorderingsystem.FoodOrderingApplication.entity.CartItem;
import com.foodorderingsystem.FoodOrderingApplication.entity.MenuItem;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.exception.ResourceNotFoundException;
import com.foodorderingsystem.FoodOrderingApplication.exception.UnauthorizedAccessException;
import com.foodorderingsystem.FoodOrderingApplication.repository.CartRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.MenuItemRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;
    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void init() {
        cartService = new CartServiceImpl(cartRepository, null, menuItemRepository, userRepository);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    private void mockAuthenticatedUser(User user) {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(user);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void addToCart_shouldAddItemSuccessfully() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        MenuItem menuItem = new MenuItem();
        menuItem.setId(10L);
        menuItem.setName("Burger");

        AddToCartDTO dto = new AddToCartDTO();
        dto.setMenuItemId(menuItem.getId());
        dto.setQuantity(2);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        mockAuthenticatedUser(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(menuItemRepository.findById(menuItem.getId())).thenReturn(Optional.of(menuItem));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart result = cartService.addToCart(userId, dto);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(menuItem, result.getItems().getFirst().getMenuItem());
    }

    @Test
    void addToCart_shouldThrowIfNotAuthenticated() {
        AddToCartDTO dto = new AddToCartDTO();
        dto.setMenuItemId(1L);
        dto.setQuantity(1);

        assertThrows(UnauthorizedAccessException.class, () -> cartService.addToCart(1L, dto));
    }

    @Test
    void viewCart_shouldReturnCart() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("view@example.com");

        Cart cart = new Cart();
        cart.setUser(user);

        mockAuthenticatedUser(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        Cart result = cartService.viewCart(userId);
        assertNotNull(result);
        assertEquals(user, result.getUser());
    }

    @Test
    void viewCart_shouldThrowIfUnauthorized() {
        User user = new User();
        user.setId(1L);
        user.setEmail("unauth@example.com");

        mockAuthenticatedUser(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedAccessException.class, () -> cartService.viewCart(999L));
    }

    @Test
    void removeFromCart_shouldRemoveSuccessfully() {
        Long userId = 1L;
        Long cartItemId = 100L;

        User user = new User();
        user.setId(userId);
        user.setEmail("remove@example.com");

        CartItem item = new CartItem();
        item.setId(cartItemId);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>(List.of(item)));

        mockAuthenticatedUser(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart result = cartService.removeFromCart(userId, cartItemId);

        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void removeFromCart_shouldThrowIfItemNotFound() {
        Long userId = 1L;
        Long cartItemId = 999L;

        User user = new User();
        user.setId(userId);
        user.setEmail("removefail@example.com");

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        mockAuthenticatedUser(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThrows(ResourceNotFoundException.class, () -> cartService.removeFromCart(userId, cartItemId));
    }

    @Test
    void clearCart_shouldClearSuccessfully() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("clear@example.com");

        CartItem item = new CartItem();
        item.setId(10L);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>(List.of(item)));

        mockAuthenticatedUser(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        cartService.clearCart(userId);

        verify(cartRepository).save(cart);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void clearCart_shouldThrowIfCartNotFound() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("nocart@example.com");

        mockAuthenticatedUser(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.clearCart(userId));
    }
}
