package com.foodorderingsystem.FoodOrderingApplication.controller;

import com.foodorderingsystem.FoodOrderingApplication.dto.AddToCartDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.Cart;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }

    @PostMapping
    public ResponseEntity<Cart> addToCart(@RequestBody AddToCartDTO dto) {
        User user = getAuthenticatedUser();
        Cart cart = cartService.addToCart(user.getId(), dto);
        return ResponseEntity.ok(cart);
    }

    @GetMapping
    public ResponseEntity<Cart> viewCart() {
        User user = getAuthenticatedUser();
        Cart cart = cartService.viewCart(user.getId());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Cart> removeFromCart(@PathVariable Long cartItemId) {
        User user = getAuthenticatedUser();
        Cart cart = cartService.removeFromCart(user.getId(), cartItemId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    public ResponseEntity<String> clearCart() {
        User user = getAuthenticatedUser();
        cartService.clearCart(user.getId());
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
