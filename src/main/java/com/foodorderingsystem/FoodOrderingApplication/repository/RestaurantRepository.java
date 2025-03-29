package com.foodorderingsystem.FoodOrderingApplication.repository;

import com.foodorderingsystem.FoodOrderingApplication.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> { }