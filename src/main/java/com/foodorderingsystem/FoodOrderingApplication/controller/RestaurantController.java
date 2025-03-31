package com.foodorderingsystem.FoodOrderingApplication.controller;

import com.foodorderingsystem.FoodOrderingApplication.dto.MenuItemDTO;
import com.foodorderingsystem.FoodOrderingApplication.dto.RestaurantDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.MenuItem;
import com.foodorderingsystem.FoodOrderingApplication.entity.Restaurant;
import com.foodorderingsystem.FoodOrderingApplication.service.MenuItemService;
import com.foodorderingsystem.FoodOrderingApplication.service.RestaurantService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    public RestaurantController(RestaurantService restaurantService, MenuItemService menuItemService) {
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
    }

    @PostMapping
    public Restaurant addRestaurant(@RequestBody RestaurantDTO dto) {
        System.out.println(SecurityContextHolder.getContext());
        return restaurantService.addRestaurant(dto);
    }

    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @PostMapping("/{restaurantId}/menu")
    public MenuItem addMenuItem(@PathVariable Long restaurantId, @RequestBody MenuItemDTO dto) {
        return menuItemService.addMenuItem(restaurantId, dto);
    }

    @GetMapping("/{restaurantId}/menu")
    public List<MenuItem> getMenuItems(@PathVariable Long restaurantId) {
        return menuItemService.getMenuItems(restaurantId);
    }
}

