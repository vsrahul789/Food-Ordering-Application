package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.dto.AddToCartDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.*;
import com.foodorderingsystem.FoodOrderingApplication.exception.ResourceNotFoundException;
import com.foodorderingsystem.FoodOrderingApplication.exception.UserNotFoundException;
import com.foodorderingsystem.FoodOrderingApplication.repository.*;
import com.foodorderingsystem.FoodOrderingApplication.service.CartService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.foodorderingsystem.FoodOrderingApplication.exception.UnauthorizedAccessException;

import java.util.ArrayList;

@Service
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, MenuItemRepository menuItemRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.menuItemRepository = menuItemRepository;
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

    @Override
    public Cart addToCart(Long userId, AddToCartDTO dto) {
        User user = getAuthenticatedUser();
        validateUserAccess(user, userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(user));

        MenuItem menuItem = menuItemRepository.findById(dto.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu Item not found"));

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setMenuItem(menuItem);
        item.setQuantity(dto.getQuantity());

        cart.getItems().add(item);
        log.info("User {} added {} items {} to cart", user.getName(), dto.getQuantity(), menuItem.getName());
        cartRepository.save(cart);
        return cart;
    }

    private Cart createNewCart(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setItems(new ArrayList<>());
        return newCart;
    }

    @Override
    public Cart viewCart(Long userId) {
        User user = getAuthenticatedUser();
        validateUserAccess(user, userId);
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    private void validateUserAccess(User user, Long userId) {
        if (!user.getId().equals(userId)) {
            throw new UnauthorizedAccessException("You can only modify your own cart!");
        }
    }


    @Override
    public Cart removeFromCart(Long userId, Long cartItemId) {
        User user = getAuthenticatedUser();
        validateUserAccess(user, userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));

        if (!removed) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        log.info("User {} removed item {} from cart", user.getName(), cartItemId);
        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(Long userId) {
        User user = getAuthenticatedUser();
        validateUserAccess(user, userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("User {} cleared their cart", user.getName());
    }
}
