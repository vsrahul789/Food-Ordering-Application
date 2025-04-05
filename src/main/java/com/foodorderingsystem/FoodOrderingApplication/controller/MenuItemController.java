package com.foodorderingsystem.FoodOrderingApplication.controller;

import com.foodorderingsystem.FoodOrderingApplication.dto.MenuItemDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.MenuItem;
import com.foodorderingsystem.FoodOrderingApplication.service.MenuItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
public class MenuItemController {

    private final MenuItemService menuItemService;
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<?> addMenuItem(@PathVariable Long restaurantId, @RequestBody MenuItemDTO dto) {
        MenuItem menuItem = menuItemService.addMenuItem(restaurantId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItem);
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItem>> getMenuItems(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuItemService.getMenuItems(restaurantId));
    }

    @PutMapping("/{restaurantId}/menu/{menuItemId}")
    public ResponseEntity<?> updateMenuItem(@PathVariable Long menuItemId, @RequestBody MenuItemDTO dto) {
        MenuItem menuItem = menuItemService.updateMenuItem(menuItemId, dto);
        return ResponseEntity.ok(menuItem);
    }

    @DeleteMapping("/{restaurantId}/menu/{menuItemId}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long menuItemId) {
        menuItemService.deleteMenuItem(menuItemId);
        return ResponseEntity.ok("Menu item deleted successfully");
    }
}
