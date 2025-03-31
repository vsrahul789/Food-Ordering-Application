package com.foodorderingsystem.FoodOrderingApplication.service;

import com.foodorderingsystem.FoodOrderingApplication.dto.MenuItemDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.MenuItem;

import java.util.List;

public interface MenuItemService {
    MenuItem addMenuItem(Long restaurantId, MenuItemDTO dto);
    List<MenuItem> getMenuItems(Long restaurantId);
    MenuItem updateMenuItem(Long menuItemId, MenuItemDTO dto);
    void deleteMenuItem(Long menuItemId);
}
