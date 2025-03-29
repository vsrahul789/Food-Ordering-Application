package com.foodorderingsystem.FoodOrderingApplication.controller;

import com.foodorderingsystem.FoodOrderingApplication.dto.AddToCartDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.Cart;
import com.foodorderingsystem.FoodOrderingApplication.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{userId}/add")
    public Cart addToCart(@PathVariable Long userId, @RequestBody AddToCartDTO dto) {
        return cartService.addToCart(userId, dto);
    }

    @GetMapping("/{userId}")
    public Cart viewCart(@PathVariable Long userId) {
        return cartService.viewCart(userId);
    }

    @DeleteMapping("/{userId}/remove/{cartItemId}")
    public Cart removeFromCart(@PathVariable Long userId, @PathVariable Long cartItemId) {
        return cartService.removeFromCart(userId, cartItemId);
    }

    @DeleteMapping("/{userId}/clear")
    public String clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return "Cart cleared successfully";
    }
}
