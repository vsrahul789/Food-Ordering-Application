package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.dto.MenuItemDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.MenuItem;
import com.foodorderingsystem.FoodOrderingApplication.entity.Restaurant;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.exception.ResourceNotFoundException;
import com.foodorderingsystem.FoodOrderingApplication.exception.UnauthorizedAccessException;
import com.foodorderingsystem.FoodOrderingApplication.repository.MenuItemRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.RestaurantRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuItemServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthenticatedUser(User user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void addMenuItem_shouldAddSuccessfully() {
        Long restaurantId = 1L;

        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setEmail("owner@example.com");

        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setId(restaurantId);
        mockRestaurant.setRestaurantOwner(mockUser);

        MenuItemDTO dto = new MenuItemDTO();
        dto.setName("Burger");
        dto.setDescription("Cheesy delight");
        dto.setPrice(9.99);

        mockAuthenticatedUser(mockUser);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(mockRestaurant));
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(mockUser));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(inv -> inv.getArgument(0));

        MenuItem result = menuItemService.addMenuItem(restaurantId, dto);

        assertNotNull(result);
        assertEquals("Burger", result.getName());
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    @Test
    void addMenuItem_shouldThrowIfNotOwner() {
        Long restaurantId = 1L;

        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setEmail("notOwner@example.com");

        User actualOwner = new User();
        actualOwner.setId(20L);

        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setId(restaurantId);
        mockRestaurant.setRestaurantOwner(actualOwner);

        mockAuthenticatedUser(mockUser);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(mockRestaurant));
        when(userRepository.findByEmail("notOwner@example.com")).thenReturn(Optional.of(mockUser));

        MenuItemDTO dto = new MenuItemDTO();

        assertThrows(UnauthorizedAccessException.class, () -> menuItemService.addMenuItem(restaurantId, dto));
    }

    @Test
    void getMenuItems_shouldReturnList() {
        Long restaurantId = 1L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Testaurant");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        MenuItem item1 = new MenuItem();
        item1.setName("Pizza");

        MenuItem item2 = new MenuItem();
        item2.setName("Fries");

        when(menuItemRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(item1, item2));

        List<MenuItem> result = menuItemService.getMenuItems(restaurantId);

        assertEquals(2, result.size());
        verify(menuItemRepository).findByRestaurantId(restaurantId);
    }

    @Test
    void updateMenuItem_shouldUpdateSuccessfully() {
        Long menuItemId = 1L;

        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setEmail("owner@example.com");

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantOwner(mockUser);

        MenuItem existingItem = new MenuItem();
        existingItem.setId(menuItemId);
        existingItem.setName("Old Name");
        existingItem.setPrice(5.0);
        existingItem.setRestaurant(restaurant);

        MenuItemDTO dto = new MenuItemDTO();
        dto.setName("New Name");
        dto.setDescription("Updated description");
        dto.setPrice(7.5);

        mockAuthenticatedUser(mockUser);

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(existingItem));
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(mockUser));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(inv -> inv.getArgument(0));

        MenuItem result = menuItemService.updateMenuItem(menuItemId, dto);

        assertEquals("New Name", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(7.5, result.getPrice());
    }

    @Test
    void deleteMenuItem_shouldDeleteSuccessfully() {
        Long menuItemId = 1L;

        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setEmail("owner@example.com");

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantOwner(mockUser);

        MenuItem item = new MenuItem();
        item.setId(menuItemId);
        item.setName("Samosa");
        item.setRestaurant(restaurant);

        mockAuthenticatedUser(mockUser);

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(item));
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(mockUser));

        menuItemService.deleteMenuItem(menuItemId);

        verify(menuItemRepository).delete(item);
    }

    @Test
    void deleteMenuItem_shouldThrowIfNotOwner() {
        Long menuItemId = 2L;

        User notOwner = new User();
        notOwner.setId(10L);
        notOwner.setEmail("notOwner@example.com");

        User actualOwner = new User();
        actualOwner.setId(99L);

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantOwner(actualOwner);

        MenuItem item = new MenuItem();
        item.setId(menuItemId);
        item.setRestaurant(restaurant);

        mockAuthenticatedUser(notOwner);

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(item));
        when(userRepository.findByEmail("notOwner@example.com")).thenReturn(Optional.of(notOwner));

        assertThrows(UnauthorizedAccessException.class, () -> menuItemService.deleteMenuItem(menuItemId));
    }

    @Test
    void deleteMenuItem_shouldThrowIfNotFound() {
        when(menuItemRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuItemService.deleteMenuItem(404L));
    }
}
