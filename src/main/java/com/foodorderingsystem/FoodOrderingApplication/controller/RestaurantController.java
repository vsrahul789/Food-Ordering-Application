package com.foodorderingsystem.FoodOrderingApplication.controller;

import com.foodorderingsystem.FoodOrderingApplication.dto.MenuItemDTO;
import com.foodorderingsystem.FoodOrderingApplication.dto.RestaurantDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.MenuItem;
import com.foodorderingsystem.FoodOrderingApplication.entity.Restaurant;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.service.MenuItemService;
import com.foodorderingsystem.FoodOrderingApplication.service.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;


    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
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
    public ResponseEntity<?> addRestaurant(@RequestBody RestaurantDTO dto) {
        Restaurant restaurant = restaurantService.addRestaurant(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurant);
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable Long restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok("Restaurant deleted successfully");
    }


}

