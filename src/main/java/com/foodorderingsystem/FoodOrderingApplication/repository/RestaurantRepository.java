package com.foodorderingsystem.FoodOrderingApplication.repository;

import com.foodorderingsystem.FoodOrderingApplication.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByRestaurantOwnerId(Long ownerId);
}