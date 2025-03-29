package com.foodorderingsystem.FoodOrderingApplication.service;

import com.foodorderingsystem.FoodOrderingApplication.dto.AddToCartDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.Cart;

public interface CartService {
    Cart addToCart(Long userId, AddToCartDTO dto);
    Cart viewCart(Long userId);
    Cart removeFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
}

