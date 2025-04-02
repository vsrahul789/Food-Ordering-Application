package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.dto.MenuItemDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.MenuItem;
import com.foodorderingsystem.FoodOrderingApplication.entity.Restaurant;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.exception.ResourceNotFoundException;
import com.foodorderingsystem.FoodOrderingApplication.exception.UnauthorizedAccessException;
import com.foodorderingsystem.FoodOrderingApplication.exception.UserNotFoundException;
import com.foodorderingsystem.FoodOrderingApplication.repository.MenuItemRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.RestaurantRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.UserRepository;
import com.foodorderingsystem.FoodOrderingApplication.service.MenuItemService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        log.error("Principal: {}", principal.getClass().getName());
        if (principal instanceof User) {
            String email = ((User) principal).getEmail();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
        } else {
            throw new UnauthorizedAccessException("Invalid authentication principal");
        }
    }

    private void validateRestaurantOwner(User user, Restaurant restaurant) {
        if (!restaurant.getRestaurantOwner().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You can only manage your own restaurant's menu items!");
        }
    }

    @Override
    public MenuItem addMenuItem(Long restaurantId, MenuItemDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));


        User owner = getAuthenticatedUser();
        validateRestaurantOwner(owner, restaurant);

        MenuItem menuItem = new MenuItem();
        menuItem.setName(dto.getName());
        menuItem.setDescription(dto.getDescription());
        menuItem.setPrice(dto.getPrice());
        menuItem.setRestaurant(restaurant);

        log.info("User {} added menu item '{}' to restaurant {}", owner.getName(), dto.getName(), restaurantId);
        return menuItemRepository.save(menuItem);
    }

    @Override
    public List<MenuItem> getMenuItems(Long restaurantId) {
        log.info("Fetching menu items for restaurant {}", restaurantRepository.findById(restaurantId).get().getName());
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public MenuItem updateMenuItem(Long menuItemId, MenuItemDTO dto) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        User owner = getAuthenticatedUser();
        validateRestaurantOwner(owner, menuItem.getRestaurant());

        menuItem.setName(dto.getName());
        menuItem.setDescription(dto.getDescription());
        menuItem.setPrice(dto.getPrice());

        log.info("User {} updated menu item '{}' (ID: {})", owner.getName(), dto.getName(), menuItemId);
        return menuItemRepository.save(menuItem);
    }

    @Override
    public void deleteMenuItem(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        User owner = getAuthenticatedUser();
        validateRestaurantOwner(owner, menuItem.getRestaurant());

        menuItemRepository.delete(menuItem);
        log.info("User {} deleted menu item '{}' (ID: {})", owner.getEmail(), menuItem.getName(), menuItemId);
    }

}
