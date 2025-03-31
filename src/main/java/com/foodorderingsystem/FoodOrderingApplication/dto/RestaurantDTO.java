package com.foodorderingsystem.FoodOrderingApplication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantDTO {
    private String name;
    private Long ownerId;
    private String address;
}