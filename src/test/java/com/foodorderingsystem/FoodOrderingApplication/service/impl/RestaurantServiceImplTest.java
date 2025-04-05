package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.dto.RestaurantDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.Restaurant;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.exception.ResourceNotFoundException;
import com.foodorderingsystem.FoodOrderingApplication.repository.RestaurantRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.UserRepository;
import com.foodorderingsystem.FoodOrderingApplication.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantService restaurantService;

    @BeforeEach
    void init() {
        restaurantService = new RestaurantServiceImpl(restaurantRepository, userRepository);
    }

    @Test
    void addRestaurant_shouldAddSuccessfully() {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setOwnerId(1L);
        dto.setAddress("123 Main Street");
        dto.setName("Pizza Palace");

        User mockUser = new User();
        mockUser.setId(1L);
        when(userRepository.findById(dto.getOwnerId())).thenReturn(Optional.of(mockUser));

        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setId(10L);
        mockRestaurant.setName(dto.getName());
        mockRestaurant.setRestaurantOwner(mockUser);
        mockRestaurant.setAddress(dto.getAddress());

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(mockRestaurant);

        Restaurant result = restaurantService.addRestaurant(dto);

        assertNotNull(result);
        assertEquals("Pizza Palace", result.getName());
        assertEquals(mockUser, result.getRestaurantOwner());
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void addRestaurant_shouldThrowIfUserNotFound() {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setOwnerId(99L);
        dto.setAddress("321 Side Ave");
        dto.setName("Burger King");

        when(userRepository.findById(dto.getOwnerId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.addRestaurant(dto));
    }

    @Test
    void getAllRestaurants_shouldReturnList() {
        Restaurant r1 = new Restaurant();
        r1.setName("Cafe One");
        Restaurant r2 = new Restaurant();
        r2.setName("Deli Two");

        when(restaurantRepository.findAll()).thenReturn(List.of(r1, r2));

        List<Restaurant> result = restaurantService.getAllRestaurants();

        assertEquals(2, result.size());
        verify(restaurantRepository).findAll();
    }

    @Test
    void deleteRestaurant_shouldDeleteIfExists() {
        Long id = 5L;
        Restaurant r = new Restaurant();
        r.setId(id);
        r.setName("Temp Delete");

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(r));

        restaurantService.deleteRestaurant(id);

        verify(restaurantRepository).deleteById(id);
    }

    @Test
    void deleteRestaurant_shouldThrowIfNotFound() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.deleteRestaurant(99L));
    }
}
