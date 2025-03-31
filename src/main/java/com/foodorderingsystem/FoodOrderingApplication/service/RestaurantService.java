package com.foodorderingsystem.FoodOrderingApplication.service;


import com.foodorderingsystem.FoodOrderingApplication.dto.MenuItemDTO;
import com.foodorderingsystem.FoodOrderingApplication.dto.RestaurantDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.MenuItem;
import com.foodorderingsystem.FoodOrderingApplication.entity.Restaurant;

import java.util.List;

public interface RestaurantService {
    Restaurant addRestaurant(RestaurantDTO dto);
    List<Restaurant> getAllRestaurants();
}