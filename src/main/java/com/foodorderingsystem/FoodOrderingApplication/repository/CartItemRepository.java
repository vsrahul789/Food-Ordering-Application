package com.foodorderingsystem.FoodOrderingApplication.repository;

import com.foodorderingsystem.FoodOrderingApplication.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> { }
