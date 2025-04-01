package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.dto.AddToCartDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.*;
import com.foodorderingsystem.FoodOrderingApplication.repository.*;
import com.foodorderingsystem.FoodOrderingApplication.service.CartService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, MenuItemRepository menuItemRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        System.out.println(SecurityContextHolder.getContext());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new IllegalStateException("User is not authenticated");
        }
    }

    @Override
    public Cart addToCart(Long userId, AddToCartDTO dto) {
        User user = getAuthenticatedUser();
        if (!user.getId().equals(userId)) {
            throw new RuntimeException("You can only add items to your own cart!");
        }

        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
            newCart.setItems(new ArrayList<>());
            return newCart;
        });

        MenuItem menuItem = menuItemRepository.findById(dto.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu Item not found"));

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setMenuItem(menuItem);
        item.setQuantity(dto.getQuantity());

        cart.getItems().add(item);
        cartRepository.save(cart);
        return cart;
    }

    @Override
    public Cart viewCart(Long userId) {
        User user = getAuthenticatedUser();
        if (!user.getId().equals(userId)) {
            throw new RuntimeException("You can only view your own cart!");
        }
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    @Override
    public Cart removeFromCart(Long userId, Long cartItemId) {
        User user = getAuthenticatedUser();
        if (!user.getId().equals(userId)) {
            throw new RuntimeException("You can only remove items from your own cart!");
        }
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        cartRepository.save(cart);
        return cart;
    }

    @Override
    public void clearCart(Long userId) {
        User user = getAuthenticatedUser();
        if (!user.getId().equals(userId)) {
            throw new RuntimeException("You can only clear your own cart!");
        }
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
