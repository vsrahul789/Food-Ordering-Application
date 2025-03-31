package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.dto.MenuItemDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.MenuItem;
import com.foodorderingsystem.FoodOrderingApplication.entity.Restaurant;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.repository.MenuItemRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.RestaurantRepository;
import com.foodorderingsystem.FoodOrderingApplication.service.MenuItemService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    private User getAuthenticatedUser() {
        System.out.println(SecurityContextHolder.getContext());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new IllegalStateException("User is not authenticated");
        }
    }

    @Override
    public MenuItem addMenuItem(Long restaurantId, MenuItemDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        User owner = getAuthenticatedUser();
        if (!restaurant.getRestaurantOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You can only add menu items to your own restaurant!");
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setName(dto.getName());
        menuItem.setDescription(dto.getDescription());
        menuItem.setPrice(dto.getPrice());
        menuItem.setRestaurant(restaurant);

        return menuItemRepository.save(menuItem);
    }

    @Override
    public List<MenuItem> getMenuItems(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public MenuItem updateMenuItem(Long menuItemId, MenuItemDTO dto) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        User owner = getAuthenticatedUser();
        if (!menuItem.getRestaurant().getRestaurantOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You can only update your own restaurant’s menu items!");
        }

        menuItem.setName(dto.getName());
        menuItem.setDescription(dto.getDescription());
        menuItem.setPrice(dto.getPrice());

        return menuItemRepository.save(menuItem);
    }

    @Override
    public void deleteMenuItem(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        User owner = getAuthenticatedUser();
        if (!menuItem.getRestaurant().getRestaurantOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You can only delete your own restaurant’s menu items!");
        }

        menuItemRepository.delete(menuItem);
    }

}
