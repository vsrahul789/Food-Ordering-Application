package com.foodorderingsystem.FoodOrderingApplication.service.impl;


import com.foodorderingsystem.FoodOrderingApplication.dto.MenuItemDTO;
import com.foodorderingsystem.FoodOrderingApplication.dto.RestaurantDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.MenuItem;
import com.foodorderingsystem.FoodOrderingApplication.entity.Restaurant;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.repository.MenuItemRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.RestaurantRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.UserRepository;
import com.foodorderingsystem.FoodOrderingApplication.service.RestaurantService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Restaurant addRestaurant(RestaurantDTO dto) {
        int ownerId = dto.getOwnerId().intValue();
        if (userRepository.findById((long) ownerId).isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User owner = userRepository.findById((long) ownerId).get();
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setRestaurantOwner(owner);
        restaurant.setAddress(dto.getAddress());
        return restaurantRepository.save(restaurant);
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }
}
