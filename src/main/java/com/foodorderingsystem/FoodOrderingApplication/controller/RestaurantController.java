package com.foodorderingsystem.FoodOrderingApplication.controller;

import com.foodorderingsystem.FoodOrderingApplication.dto.MenuItemDTO;
import com.foodorderingsystem.FoodOrderingApplication.dto.RestaurantDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.MenuItem;
import com.foodorderingsystem.FoodOrderingApplication.entity.Restaurant;
import com.foodorderingsystem.FoodOrderingApplication.service.RestaurantService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Restaurant addRestaurant(@RequestBody RestaurantDTO dto) {
        return restaurantService.addRestaurant(dto);
    }

    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @PostMapping("/{restaurantId}/menu")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public MenuItem addMenuItem(@PathVariable Long restaurantId, @RequestBody MenuItemDTO dto) {
        return restaurantService.addMenuItem(restaurantId, dto);
    }

    @GetMapping("/{restaurantId}/menu")
    public List<MenuItem> getMenuItems(@PathVariable Long restaurantId) {
        return restaurantService.getMenuItems(restaurantId);
    }
}

