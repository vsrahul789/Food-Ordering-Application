package com.foodorderingsystem.FoodOrderingApplication.repository;

import com.foodorderingsystem.FoodOrderingApplication.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurantId(Long restaurantId);
}
